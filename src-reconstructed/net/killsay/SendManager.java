package net.killsay;

/**
 * Reconstructed audit source, not original source.
 *
 * Evidence map:
 * - This is not the fake msgQueue/lastSendTime version.
 * - Current Simple has only cooldownUntil and direct send().
 * - The "/" command split maps to LTSC ClientSendGateway.sendChatOrCommand().
 */
public final class SendManager {
    private long cooldownUntil;

    void send(Object minecraftClient, String text, double cooldownSeconds, long now) {
        if (now < cooldownUntil) {
            return;
        }

        /*
         * Bytecode behavior:
         * if text.startsWith("/"):
         *   networkHandler.method_45730(text.substring(1))
         * else:
         *   networkHandler.method_45729(text)
         */

        cooldownUntil = now + (long) (cooldownSeconds * 1000.0D);
    }
}

