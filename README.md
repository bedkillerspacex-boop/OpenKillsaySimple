# OpenKillsaySimple

> OpenZen 风格的 KillsaySimple 开盒仓库。  
> 这里不是发布可用 Mod，而是公开 `KillsaySimple 1.0` 的结构、伪源码、证据链和一点必要的互联网乐子。

![OpenKillsaySimple](assets/openkillsaysimple.jpg)

## 这是什么

`KillsaySimple` 对外叫 Simple，但从 JAR 结构看，它并不是一个干净的独立实现。

本仓库整理了：

- `src-reconstructed/`：基于 `javap` 字节码和 JAR 结构复原的关键源码/伪源码
- `paste/README.md`：对 `KillsaySimple` 与 `KillsayReborn LTSC 2.0` 的证据对比
- `FUN.md`：整活内容，给小猫弟嘴硬时配套食用
- `assets/openkillsaysimple.jpg`：根 README 展示图

## 证据入口

主要证据在这里：

[paste/README.md](paste/README.md)

最核心的结论：

```text
KillsaySimple
  = LTSC 的 Mixin 主入口
  + LTSC 的 KillTracker pending/chatWatch 状态
  + LTSC 的 MinecraftWorldProbe 世界探针逻辑
  + LTSC 的 ClientSendGateway 发包分流
  + LTSC 的 WebGuiServer 本地 HTTP 思路
  - HUD / 自动回复 / 自动举报 / 云文本 / 歌词 / NoMove / 延迟队列
```

这不是逐字节整包复制。  
它更像是裁剪、压扁、改名、换皮、嘴硬。

## 仓库结构

```text
OpenKillsaySimple/
├── README.md
├── FUN.md
├── assets/
│   └── openkillsaysimple.jpg
├── paste/
│   └── README.md
├── evidence/
│   ├── README.md
│   ├── similarity_report.md
│   ├── jar-tree/
│   │   ├── simple_classes.txt
│   │   └── ltsc_classes.txt
│   └── javap-simple/
│       └── *.txt
└── src-reconstructed/
    └── net/killsay/
        ├── Config.java
        ├── KillTracker.java
        ├── KillsayMod.java
        ├── SendManager.java
        ├── WebGui.java
        └── mixin/
            ├── ClientPlayNetworkHandlerMixin.java
            └── MinecraftClientMixin.java
```

## 关于源码

`src-reconstructed/` 不是官方源码，也不是可直接编译的项目。

它的用途是：

- 让读者不用啃 `.class` 和 `javap` 输出也能看懂结构
- 标出哪些逻辑来自 Simple 当前 JAR
- 标出哪些点与 LTSC 2.0 的类存在结构对应

如果要做严格法律/维权用途，请以原始 JAR、反汇编输出、hash、refmap 和字节码为准。

## 一句话

小猫弟说这是 Simple。  
字节码说这是 LTSC 的骨头架子被拆下来重新刷漆。
