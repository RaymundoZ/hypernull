package ru.croccode.hypernull.bot.mybot;

import ru.croccode.hypernull.bot.Bot;
import ru.croccode.hypernull.bot.BotMatchRunner;
import ru.croccode.hypernull.domain.MatchMode;
import ru.croccode.hypernull.geometry.Point;
import ru.croccode.hypernull.geometry.Size;
import ru.croccode.hypernull.io.SocketSession;
import ru.croccode.hypernull.message.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Set;

public class MyBot implements Bot {

    private int id;
    private int viewRadius;
    private Size size;

    @Override
    public Register onHello(Hello hello) {
        Register register = new Register();
        register.setBotName("Genius");
        register.setBotSecret("322");
        register.setMode(MatchMode.FRIENDLY);
        return register;
    }

    @Override
    public void onMatchStarted(MatchStarted matchStarted) {
        id = matchStarted.getYourId();
        viewRadius = matchStarted.getViewRadius();
        size = matchStarted.getMapSize();
    }

    @Override
    public Move onUpdate(Update update) {
        Set<Point> blocks = update.getBlocks();
        Set<Point> coins = update.getCoins();
        Point bot = update.getBots().get(id);
        Strategy strategy = new Strategy(size, viewRadius, bot, coins, blocks);
        return strategy.getMove();
    }

    @Override
    public void onMatchOver(MatchOver matchOver) {

    }

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        socket.setTcpNoDelay(true);
        socket.setSoTimeout(300_000);
        socket.connect(new InetSocketAddress("localhost", 2021));

        SocketSession session = new SocketSession(socket);
        MyBot bot = new MyBot();
        new BotMatchRunner(bot, session).run();
    }

}
