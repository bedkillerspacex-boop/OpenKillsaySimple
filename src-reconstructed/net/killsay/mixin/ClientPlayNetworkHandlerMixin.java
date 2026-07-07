package net.killsay.mixin;

/**
 * Reconstructed audit source, not original source.
 */
public final class ClientPlayNetworkHandlerMixin {
    private void killsay$onTitle(Object packet, Object ci) {
        /*
         * Bytecode behavior:
         * - null guard
         * - packet.comp_2281()
         * - Text.getString()
         * - KillsayMod.onTitleReceived(text)
         */
    }

    private void killsay$onGameMessage(Object packet, Object ci) {
        /*
         * Bytecode behavior:
         * - null guard
         * - MinecraftClient.getInstance()
         * - packet.comp_763()
         * - Text.getString()
         * - KillsayMod.onChatMessage(text, client)
         */
    }
}

