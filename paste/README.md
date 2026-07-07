# KillsaySimple 对 KillsayReborn LTSC 2.0 的抄袭/套壳证据

> 对比对象：
>
> - `KillsaySimple`：`KillsayReborn-1.0.0.jar`
> - `KillsayReborn LTSC 2.0`：`KillsayReborn-LTSC-2.0-dickfix (1).jar`
>
> 结论：`KillsaySimple` 不是“自己写了一个同类 Mod”这么简单。  
> 它把 LTSC 2.0 的核心模块拆开、合并、改名、删功能，再加上一个更粗糙的 Web GUI，包装成所谓 `Killsay Simple`。

## 0. 先纠正一个口径

上一版把 `CIT-` 写得太重了。  
`CIT-` 只能算旁证，不能单独算实锤。

这次还核对了 Gemini 文档里的 50 条指纹。里面有些点能用，但也有不少和当前这两个 JAR 对不上。为了防止反被对面抓漏洞，本文只采用能从 `KillsayReborn-1.0.0.jar` 和 `KillsayReborn-LTSC-2.0-dickfix (1).jar` 直接核验的内容。

Gemini 文档中不应直接采用的点包括：

```text
fabricloader >=0.15.0 / minecraft ~1.20.1 / java >=17
uuidget.mixins.json 在 Simple 中残留
Simple 内有 assets/uuidget/textures/gui/sprites/hud_bg.png
PendingVictim(victimName, lastHitTime, possibleKiller)
pendingVictims = ConcurrentHashMap
SendManager.INSTANCE / msgQueue / lastSendTime / addToQueue / clearQueue
[PLAYER] / [KILLER] / killsay.json / 8192 buffer / 256 截断
GameOptionsAccessor / MessageHandlerMixin / InGameHudMixin / KeyBindingAccessor 存在于 Simple
```

这些在 Simple 1.0 当前 JAR 中没有出现。  
能采用的，是下面这些结构性证据。

真正能打的证据不是一个字符串，而是这些结构性对应：

| KillsaySimple | KillsayReborn LTSC 2.0 | 结论 |
|---|---|---|
| `net.killsay.KillTracker` | `KillTracker` + `MinecraftWorldProbe` | Simple 把 LTSC 的追踪状态和世界探针压进一个类 |
| `net.killsay.SendManager` | `ClientSendGateway` + `CooldownManager`/`SendManager` | Simple 把 LTSC 的发送网关和冷却逻辑压扁 |
| `net.killsay.WebGui` | `WebGuiServer` | Simple 把 LTSC 的内嵌 WebGuiServer 改成资源文件版，并删功能 |
| `ClientPlayNetworkHandlerMixin` | 同名 Mixin | title 注入流程几乎同构 |
| `MinecraftClientMixin` | 同名 Mixin | tick 主循环挂钩一致 |
| `fabric.mod.json` | `fabric.mod.json` | 描述、依赖、环境、版本要求高度一致 |

所以本文不靠“感觉像”，而是靠 JAR 解包、refmap、`javap` 字节码和常量池对比。

## 1. 体量对比：Simple 是裁剪包，不是独立工程

| 项目 | class 数 | 文件数 |
|---|---:|---:|
| KillsaySimple 1.0.0 | 12 | 21 |
| KillsayReborn LTSC 2.0 | 244 | 253 |

Simple 的 12 个类是：

```text
Config
Config$ProfilesData
KillTracker
KillTracker$PendingVictim
KillsayMod
KillsayMod$1
MatchTracker
SendManager
WebGui
WebGui$FieldConsumer
mixin.ClientPlayNetworkHandlerMixin
mixin.MinecraftClientMixin
```

它不是从零搭了一套架构，而是抓住 LTSC 的主链路：

```text
Mixin -> 主类 tick -> KillTracker -> SendManager -> Config/WebGui
```

然后把 LTSC 里 HUD、举报、云文本、自动回复、歌词、NoMove、延迟队列等大量功能删掉。

## 2. `fabric.mod.json`：壳换了，描述没怎么换

Simple：

```json
"id": "killsay",
"version": "1.0.0",
"name": "Killsay Simple",
"description": "Send a custom chat message when a player you attacked leaves render distance or dies.",
"authors": ["Killsay contributors"],
"license": "MIT",
"environment": "client"
```

LTSC：

```json
"id": "uuidget",
"version": "LTSC-2.0-dickfix",
"name": "Killsay Reborn",
"description": "Send a custom chat message when a player you attacked leaves render distance within 5s.",
"authors": ["KillsayReborn contributors"],
"license": "MIT",
"environment": "client"
```

依赖也同组：

```json
"fabricloader": ">=0.16.0",
"minecraft": "~1.21.4",
"java": ">=21",
"fabric-api": "*"
```

关键句式完全同源：

```text
Send a custom chat message when a player you attacked leaves render distance...
```

Simple 只是在后面补了 `or dies`。  
这不是最强证据，但它说明 Simple 的项目定位、版本目标和说明文案都沿着 LTSC 来。

## 3. refmap：同一个 `KillsayReborn-refmap.json`

Simple 的 refmap 文件名：

```text
KillsayReborn-refmap.json
```

LTSC 的 refmap 文件名：

```text
KillsayReborn-refmap.json
```

Simple 的 refmap：

```json
"net/killsay/mixin/ClientPlayNetworkHandlerMixin": {
  "onGameMessage": "Lnet/minecraft/class_634;method_43596(Lnet/minecraft/class_7439;)V",
  "onTitle": "Lnet/minecraft/class_634;method_34083(Lnet/minecraft/class_5904;)V"
},
"net/killsay/mixin/MinecraftClientMixin": {
  "tick": "Lnet/minecraft/class_310;method_1574()V"
}
```

LTSC 的 refmap：

```json
"mojang/minecraft/KillsayReborn/mixin/ClientPlayNetworkHandlerMixin": {
  "onTitle": "Lnet/minecraft/class_634;method_34083(Lnet/minecraft/class_5904;)V"
},
"mojang/minecraft/KillsayReborn/mixin/MinecraftClientMixin": {
  "tick": "Lnet/minecraft/class_310;method_1574()V"
}
```

实锤点：

- refmap 文件名没改，仍然叫 `KillsayReborn-refmap.json`
- 两边都注入 `ClientPlayNetworkHandler.method_34083(class_5904)` 读取 title
- 两边都注入 `MinecraftClient.method_1574()` 做 client tick
- Simple 把包名从 `mojang.minecraft.KillsayReborn` 改成 `net.killsay`

这不是普通 Fabric 模板能解释的相似度。

## 4. Title Mixin：控制流几乎一模一样

Simple：

```text
killsay$onTitle(class_5904, CallbackInfo)
  if packet == null return
  packet.comp_2281()
  if title == null return
  title.getString()
  KillsayMod.onTitleReceived(String)
```

LTSC：

```text
uuidget$title(class_5904, CallbackInfo)
  if packet == null return
  packet.comp_2281()
  if title == null return
  title.getString()
  ClientInitializer.onTitleReceived(String)
```

字节码顺序一致：

```text
class_5904.comp_2281()
class_2561.getString()
onTitleReceived(String)
```

Simple 做的就是：

```text
uuidget$title -> killsay$onTitle
ClientInitializer -> KillsayMod
```

## 5. Tick Mixin：同一个主循环入口

Simple：

```text
MinecraftClientMixin
  method_1574() -> KillsayMod.tick()
```

LTSC：

```text
MinecraftClientMixin
  method_1574() -> ClientInitializer.onClientTick(MinecraftClient)
```

Simple 省掉了参数传递，把逻辑塞进静态主类，但挂点仍然是同一个 Minecraft client tick。  
这说明它保留的是 LTSC 的运行模型，不只是“也写了一个 Mixin”。

## 6. `KillTracker`：Simple 把 LTSC 两个模块压成一个

Simple 的 `KillTracker` 字段：

```text
Map<UUID, PendingVictim> pending
Map<String, Long> chatWatch
```

LTSC 的 `KillTracker` 字段：

```text
Map<UUID, PendingVictim> pending
Map<String, Long> chatWatch
Map<Integer, Set<String>> trackedProjectiles
```

Simple 删除了 LTSC 的 `trackedProjectiles`，保留了最核心的：

```text
pending + chatWatch
```

这已经不是巧合，因为 Simple 的流程也对上。

## 7. `PendingVictim`：嵌套 record 对应

Simple：

```text
net.killsay.KillTracker$PendingVictim
extends java.lang.Record
private final String name
private final long deadline
constructor(String, long)
accessors: name(), deadline()
```

LTSC：

```text
mojang.minecraft.KillsayReborn.KillTracker$PendingVictim
extends java.lang.Record
private final String name
private final long deadline
constructor(String, long)
accessors: name(), deadline()
```

这个点可以作为强证据，但要写准：  
Gemini 文档里写的 `victimName / lastHitTime / possibleKiller` 三字段版本，在当前 JAR 里不存在。当前 JAR 里真正一致的是 `record PendingVictim(String name, long deadline)`。

也就是说，正确证据不是“三字段 PendingVictim”，而是“两字段 record PendingVictim”。  
它和 `pending`、`chatWatch`、`collectPending`/`collectConfirmedKills` 放一起，就是同一套追踪模型。

## 8. 世界查询逻辑：Simple 抄的是 LTSC 的 `MinecraftWorldProbe`

这是上一版漏掉的重点。

Simple 的 `KillTracker.collectConfirmedKills(...)` 里有这些调用：

```text
client.world.getPlayerByUuid(uuid)
class_638.method_18470(UUID)

player.isDead()
class_1657.method_29504()

player.getHealth() <= 0
class_1657.method_6032()

player.getRemovalReason().shouldDestroy()
class_1657.method_35049()
class_1297$class_5529.method_31486()

client.getNetworkHandler().getPlayerList()
class_310.method_1562()
class_634.method_2880()
class_640.method_2966().getName()
```

LTSC 的 `MinecraftWorldProbe.victimState(...)` 使用同一组死亡/消失判定：

```text
class_638.method_18470(UUID)
class_1657.method_35049()
class_1657.method_29504()
class_1657.method_6032()
class_1297$class_5529.method_31486()
```

LTSC 的 `MinecraftWorldProbe.isPlayerInTabList(...)` 使用同一组 Tab 查询：

```text
class_310.method_1562()
class_634.method_2880()
class_640.method_2966().getName()
```

结论很明确：

```text
Simple.KillTracker
  = LTSC.KillTracker 的 pending/chatWatch
  + LTSC.MinecraftWorldProbe 的 victimState / tabList 查询
```

这比“类名像”硬得多。

## 9. `KillTracker` 流程：只是从拆分版改成大杂烩版

LTSC 的设计是拆开的：

```text
KillTracker.collectPending(...)
KillTracker.addChatWatchIfAbsent(...)
KillTracker.collectMissingFromChatWatch(...)
MinecraftWorldProbe.victimState(...)
MinecraftWorldProbe.isPlayerInTabList(...)
```

Simple 的设计是塞进一个方法：

```text
KillTracker.collectConfirmedKills(...)
```

但流程相同：

```text
1. pending 里记录 UUID -> name/deadline
2. 超时移除
3. 通过 UUID 查世界中的玩家
4. 玩家消失则进入 chatWatch
5. 玩家死亡/血量归零/RemovalReason 销毁则确认
6. chatWatch 再查 Tab 列表
7. Tab 里还在则移除观察
8. Tab 里不在则确认击杀
```

这就是典型的“把别人拆好的模块揉成一个类”。

## 10. `CIT-`：只作旁证，不作实锤

两边都有：

```text
"CIT-"
String.startsWith(...)
```

LTSC 在：

```text
MinecraftWorldProbe.isIgnoredPlayerName(String)
KillTracker.isIgnoredName(String)
```

Simple 在：

```text
KillTracker.mark(PlayerEntity)
```

这个点不能单独证明抄袭，因为它可能来自同一个服务器环境的业务规则。  
但它和上面的 `MinecraftWorldProbe` 同组调用一起出现，就能作为辅助证据。

所以本文把 `CIT-` 降级为旁证。

## 11. `SendManager`：真正对应的是 LTSC 的 `ClientSendGateway`

上一版拿 Simple 的 `SendManager` 对 LTSC 的 `SendManager`，不够准。  
更准确的对应是：

```text
Simple.SendManager
  -> LTSC.ClientSendGateway
  -> LTSC.CooldownManager / SendManager.Session
```

Simple：

```text
if text.startsWith("/")
  networkHandler.method_45730(text.substring(1))
else
  networkHandler.method_45729(text)
```

LTSC `ClientSendGateway.sendChatOrCommand(...)`：

```text
if commandSource.startsWith("/")
  networkHandler.method_45730(commandSource.substring(1))
else
  networkHandler.method_45729(chatText)
```

完全同一条分流：

```text
"/" -> substring(1) -> method_45730(command)
else -> method_45729(chat)
```

Simple 只是把它放进了自己的 `SendManager.send(...)`。

这里也要纠正 Gemini 文档：  
当前 Simple 的 `SendManager` 里没有 `INSTANCE`、没有 `msgQueue`、没有 `lastSendTime`、没有 `Collections.synchronizedList(new ArrayList<>())`、没有 `addToQueue`、没有 `clearQueue`。

Simple 当前的真实结构是：

```text
private long cooldownUntil;
void send(MinecraftClient, String, double, long)
```

所以不能写“队列单例 100% 克隆”。  
能写、也更硬的是：`"/" -> substring(1) -> method_45730` 与普通聊天 `method_45729` 的发送网关逻辑对上 LTSC `ClientSendGateway`。

## 12. 冷却逻辑：LTSC 管线被压成一个字段

Simple：

```text
private long cooldownUntil;

if now < cooldownUntil:
  return

send(...)

cooldownUntil = now + cooldownSeconds * 1000
```

LTSC：

```text
CooldownManager.canSend(now, deadPause)
SendPipeline.send(...)
CooldownManager.startAfterSend(now, cooldownMs)
```

Simple 删掉了：

```text
SendIntent
SendPipeline
SendResult
Gufa
NoMove
deadPause
deferred queue
```

但保留了核心：

```text
冷却判断 -> 发送 -> 启动下一轮冷却
```

这不是独立架构，是把 LTSC 的发送系统砍到只剩直连发送。

## 13. Web GUI：Simple 不是凭空新增，LTSC 也有 `WebGuiServer`

上一版说 “LTSC 没有 Web GUI” 不准确。  
LTSC 没有独立 `web/index.html` 文件，但它有：

```text
mojang.minecraft.KillsayReborn.WebGuiServer
```

而且 `WebGuiServer` 内嵌了完整：

```text
INDEX_HTML
APP_CSS
APP_JS
```

LTSC WebGuiServer 的 API：

```text
GET /
GET /app.css
GET /app.js
GET /api/state
POST /api/config
Content-Type
application/json; charset=utf-8
text/html; charset=utf-8
text/css; charset=utf-8
application/javascript; charset=utf-8
```

Simple WebGui 的 API：

```text
GET /
GET /index.html
GET /style.css
GET /script.js
GET /api/config
POST /api/config
POST /api/reload
POST /api/test
GET /api/profiles
GET /api/stats
Content-Type
application/json; charset=utf-8
text/html; charset=utf-8
text/css; charset=utf-8
application/javascript; charset=utf-8
```

两边共享的关键字符串包括：

```text
/api/config
GET
POST
Content-Type
application/json; charset=utf-8
text/html; charset=utf-8
text/css; charset=utf-8
application/javascript; charset=utf-8
enabled
templates
cooldownSeconds
windowSeconds
debug
message
ok
```

Simple 的差异：

- LTSC 把 HTML/CSS/JS 内嵌在 `WebGuiServer.class`
- Simple 把 HTML/CSS/JS 拆成 `web/index.html`、`web/style.css`、`web/script.js`
- Simple 删掉 LTSC 的 HUD/举报/自动回复/云端/歌词等大部分字段
- Simple 加了 profiles/stats/test 这些自己的接口

这不是“Simple 自己从零发明 Web GUI”。  
更像是拿 LTSC 的 WebGuiServer 思路，拆资源、减功能、改字段。

## 14. 配置项：核心默认值和字段名对齐

Simple `Config`：

```text
enabled
templates
cooldownSeconds = 3.0
windowSeconds = 5.0
debug
webGuiPort
joinEnabled
joinTemplates
winEnabled
winTemplates
sendMode
```

LTSC `ClientOptions`：

```text
enabled
templates
cooldownSeconds = 3.0
windowSeconds = 5.0
debug
modLang
chatProjectileDetect
transferGuard
wanxin
gufa
gufaNoMove
cloudEnabled
```

共同核心：

```text
enabled
templates
cooldownSeconds
windowSeconds
debug
{name}
3.0
5.0
```

这些字段名本身不算很特殊。  
但它们和 `KillTracker`、`ClientSendGateway`、`WebGuiServer` 的对应关系合在一起，就不是普通撞车。

## 15. Simple 的新增内容不能都算抄

为了证据干净，下面这些不直接算抄 LTSC：

```text
profiles.json / 多配置档
MatchTracker 的 day/month/total 胜负统计
joinEnabled / joinTemplates
winTemplates 的部分 Web 配置组织
独立 web/index.html 的具体页面布局
```

这些可能是 Simple 自己加的，或者来自别处。  
本文不把它们硬扣到 LTSC 头上。

## 16. 对 Gemini 50 点的逐项取舍

Gemini 文档有参考价值，但不能整段照搬。下面是核验后的取舍：

| Gemini 说法 | 当前 JAR 核验 | 是否采用 |
|---|---|---|
| `KillsayReborn-refmap.json` 根目录残留 | Simple 和 LTSC 都有该文件名 | 采用 |
| `fabric.mod.json` 依赖为 MC 1.20.1 / Java 17 | 实际是 Minecraft `~1.21.4`、Java `>=21` | 不采用 |
| Simple 残留 `assets/uuidget/...hud_bg.png` | Simple 没有该路径，LTSC 有 | 不采用 |
| Simple 使用 `uuidget.mixins.json` | Simple 使用 `killsay.mixins.json` | 不采用 |
| `PendingVictim` 完全对应 | 对应，但字段是 `name/deadline`，不是三字段版本 | 修正后采用 |
| Mixin title/tick 注入点对应 | refmap 和字节码均能核验 | 采用 |
| GameOptions/MessageHandler/HUD/KeyBinding Mixin 也在 Simple | Simple 只有两个 Mixin | 不采用 |
| SendManager 队列单例克隆 | Simple 没有队列单例 | 不采用 |
| SendManager `/` 命令与普通聊天分流 | Simple 与 LTSC `ClientSendGateway` 对上 | 采用 |
| 配置 `[PLAYER]`/`[KILLER]`/`killsay.json` | Simple 未出现这些常量 | 不采用 |
| 语言包目录仅前缀替换 | 目录层级可比，但键值内容并不完全重合 | 作为弱证据 |
| 架构阉割：删 SMTC、自动回复、举报、HUD 等外围功能 | LTSC 有这些类，Simple 没有 | 采用 |

## 17. 真正的抄袭模式

这不是“整包逐字节复制”。  
两个 JAR 没有发现完全相同 hash 的文件。

它的模式更像这样：

```text
1. 拿 LTSC 的核心入口：
   ClientPlayNetworkHandlerMixin / MinecraftClientMixin

2. 拿 LTSC 的击杀追踪状态：
   pending / chatWatch / PendingVictim

3. 拿 LTSC 的世界探针逻辑：
   getPlayerByUuid / isDead / getHealth / RemovalReason / TabList

4. 拿 LTSC 的发送网关：
   "/" command 分流 / sendChatMessage

5. 拿 LTSC 的 WebGuiServer 思路：
   本地 HTTP server / JSON API / config 字段

6. 删除高级功能：
   HUD / 云文本 / 举报 / 自动回复 / 歌词 / NoMove / 延迟队列

7. 改包名、改 mod id、改入口类：
   mojang.minecraft.KillsayReborn -> net.killsay
   Killsay Reborn -> Killsay Simple
```

这叫“裁剪式套壳”，不是原创。

## 18. 对小猫弟和 Simple 的批判

如果小猫弟把这个项目说成：

```text
基于 KillsayReborn LTSC 2.0 的精简版
```

那至少还算把话说清楚。

但如果它以 `KillsaySimple` 的名义发布，却不明确说明：

```text
核心 KillTracker 来自 LTSC 思路
世界探针逻辑来自 LTSC MinecraftWorldProbe
发送分流来自 LTSC ClientSendGateway
WebGui 服务端路线来自 LTSC WebGuiServer
```

那就是把别人的核心劳动拆碎、改名、删功能后重新包装。

更难看的是，Simple 的中文资源和 Web 页面里还出现明显乱码：

```text
鎵撳紑 Killsay Web GUI
Minecraft 鑷姩鍠婅瘽 Mod 閰嶇疆
宸茬鐢?
```

这不像认真维护的分支，更像赶工拼装的套壳包。

## 19. 最终结论

可实锤的是：

- Simple 继承了 LTSC 的 Mixin 主入口：title 包和 client tick
- Simple 的 `KillTracker` 同时复刻/合并了 LTSC 的 `KillTracker` 和 `MinecraftWorldProbe`
- Simple 的 `SendManager` 复刻了 LTSC `ClientSendGateway` 的 `/` 命令分流
- Simple 的冷却发送模型是 LTSC 发送管线的压扁版
- Simple 的 WebGui 不是凭空新增，而是对应 LTSC 的 `WebGuiServer` 本地 HTTP + JSON API 路线
- Simple 的配置核心字段和默认值与 LTSC 对齐

不能实锤的是：

- `CIT-` 不能单独算抄，只能当旁证
- Simple 的 profiles/stats/test 接口不能直接算抄 LTSC
- Simple 的独立 HTML/CSS/JS 页面文件不是逐字节复制 LTSC

一句话：

> `KillsaySimple` 不是原封不动复制 LTSC 2.0，但它把 LTSC 2.0 的核心 killsay 链路、世界探针、发送网关和 WebGui 服务端思路拆出来，改名、压扁、删功能后重新包装。  
> 如果作者小猫弟没有清楚标注来源和改动范围，这就是不体面的裁剪式套壳。
