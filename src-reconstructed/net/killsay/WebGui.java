package net.killsay;

/**
 * Reconstructed audit source, not original source.
 *
 * Evidence map:
 * - Simple has its own WebGui class and web/ static files.
 * - LTSC has WebGuiServer with embedded INDEX_HTML / APP_CSS / APP_JS.
 * - Both share local HTTP server + JSON config route concepts.
 */
public final class WebGui {
    private static Object server;
    private static Config config;

    static void start(Config c) {
        config = c;
        /*
         * Bytecode behavior:
         * - create HttpServer
         * - create contexts:
         *   /
         *   /api/config
         *   /api/reload
         *   /api/test
         *   /api/profiles
         *   /api/stats
         * - set single-thread executor
         * - start server
         */
    }

    private static void handleConfigApi(Object exchange) {
        /*
         * GET:
         *   returns enabled/templates/cooldownSeconds/windowSeconds/debug/webGuiPort/etc.
         *
         * POST:
         *   reads JSON body
         *   applies fields into Config
         *   saves and reloads config
         */
    }
}

