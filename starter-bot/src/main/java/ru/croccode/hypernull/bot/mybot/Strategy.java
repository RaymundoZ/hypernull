package ru.croccode.hypernull.bot.mybot;

import ru.croccode.hypernull.geometry.Offset;
import ru.croccode.hypernull.geometry.Point;
import ru.croccode.hypernull.geometry.Size;
import ru.croccode.hypernull.message.Move;

import java.util.ArrayList;
import java.util.Set;

public class Strategy {

    private final Size mapSize;
    private final int viewRadius;
    private final Offset[] offsets;
    private final Point bot;
    private final Set<Point> coins;
    private final Set<Point> blocks;

    public Strategy(Size mapSize, int viewRadius, Point bot, Set<Point> coins, Set<Point> blocks) {
        this.mapSize = mapSize;
        this.viewRadius = viewRadius;
        this.bot = bot;
        this.coins = coins;
        this.blocks = blocks;
        offsets = new Offset[]{new Offset(0, 1), new Offset(1, 0),
                new Offset(1, 1), new Offset(0, -1),
                new Offset(-1, 0), new Offset(-1, -1),
                new Offset(1, -1), new Offset(-1, 1)};
    }

    public Move getMove() {
        if (coins == null) {
            return findWay();

        } else {
            Move move = new Move();
            Offset choice = null;
            Point coin = findNearestCoin();
            double currentLength = findLength(bot, coin);
            for (Offset offset : offsets) {
                double newLength = findLength(coin, bot.apply(offset, mapSize));
                if (newLength <= currentLength && checkStep(getBot().apply(offset, mapSize))) {
                    currentLength = newLength;
                    choice = offset;
                }
            }
            if (choice == null) {
                return findWay();
            }
            move.setOffset(choice);
            return move;
        }
    }

    private Move findWay() {
        Move result = null;
        int counter = 0;
        int max = 0;
        for (Offset offset : offsets) {
            Point pos = bot;
            while (isInViewRadius(pos.apply(offset, mapSize)) && checkStep(pos.apply(offset, mapSize))) {
                pos = pos.apply(offset, mapSize);
                counter++;
            }
            if (counter > max) {
                max = counter;
                result = new Move() {{
                    setOffset(offset);
                }};
            }
            counter = 0;
        }
        return result;
    }

    private boolean checkStep(Point point) {
        if (blocks == null) return true;
        return blocks.stream().noneMatch((p) -> p.x() == point.x() && p.y() == point.y());
    }

    private boolean isInViewRadius(Point point) {
        return Math.pow(findLength(bot, point), 2) <= viewRadius;
    }

    private Point findNearestCoin() {
        if (bot == null || coins == null)
            return null;
        Point min = new ArrayList<>(coins).get(0);
        double minLength = findLength(bot, min);
        for (Point p : coins) {
            double length = findLength(bot, p);
            if (length < minLength) {
                min = p;
                minLength = length;
            }
        }
        return min;
    }

    private double findLength(Point p1, Point p2) {
        int dx = Math.min(Math.abs(p1.x() - p2.x()), mapSize.width() - Math.abs(p1.x() - p2.x()));
        int dy = Math.min(Math.abs(p1.y() - p2.y()), mapSize.height() - Math.abs(p1.y() - p2.y()));

        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

    public Size getMapSize() {
        return mapSize;
    }

    public Offset[] getOffsets() {
        return offsets;
    }

    public Point getBot() {
        return bot;
    }

    public Set<Point> getCoins() {
        return coins;
    }

    public Set<Point> getBlocks() {
        return blocks;
    }
}
