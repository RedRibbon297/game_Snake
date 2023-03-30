package GameSnake2;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import static GameSnake2.Direction.*;

public class Snake {
    public Rect[] body = new Rect[100];
    public double bodyWidth, bodyHeight;

    public int size;
    public int tail = 0;
    public int head = 0;

    public Direction direction = RIGHT;

    public double ogWaitBetweenUpdates = 0.18f;
    public double waitTimeLeft = ogWaitBetweenUpdates;

    public Rect background;

    public Snake(int size, double startX, double startY, double bodyWidth, double bodyHeight, Rect background) {
        this.size = size;
        this.bodyWidth = bodyWidth;
        this.bodyHeight = bodyHeight;
        this.background = background;

        for (int i = 0; i <= size; i++) {
            Rect bodyPiece = new Rect(startX + i * bodyWidth, startY, bodyWidth, bodyHeight);
            body[i] = bodyPiece;
            head++;
        }
        head--;
    }

    public void changeDirection(Direction newDirection) {
        if (newDirection == RIGHT && direction != LEFT)
            direction = newDirection;
        else if (newDirection == LEFT && direction != RIGHT)
            direction = newDirection;
        else if (newDirection == UP && direction != DOWN)
            direction = newDirection;
        else if (newDirection == DOWN && direction != UP)
            direction = newDirection;
    }

    public void update(double dt) {
        if (waitTimeLeft > 0) {
            waitTimeLeft -= dt;
            return;
        }
        if (intersectingWithSelf()) {
            Window.getWindow().changeState(0);
        }

        waitTimeLeft = ogWaitBetweenUpdates;

        double newX = 0;
        double newY = 0;

        if (direction == RIGHT) {
            newX = body[head].x + bodyHeight;
            newY = body[head].y;
        } else if (direction == LEFT) {
            newX = body[head].x - bodyWidth;
            newY = body[head].y;
        } else if (direction == UP) {
            newX = body[head].x;
            newY = body[head].y - bodyHeight;
        } else if (direction == DOWN) {
            newX = body[head].x;
            newY = body[head].y + bodyHeight;
        }
        //[1, 1, 1, 0, 0]
        //[0, 1, 1, 1, 0]
        body[(head + 1) % body.length] = body[tail];
        body[tail] = null;
        head = (head + 1) % body.length;
        tail = (tail + 1) % body.length;

        body[head].x = newX;
        body[head].y = newY;

    }

    public boolean intersectingWithSelf() {
        Rect headR = body[head];
        return intersectingWithRect(headR) || intersectingWithScreenBoundaries(headR);
    }

    public boolean intersectingWithRect(Rect rect) {

        for (int i = tail; i != head; i = (i + 1) % body.length) {
            if (intersecting(rect, body[i])) return true;
        }

        return false;
    }

    public boolean intersecting(Rect r1, Rect r2) {
        return (r1.x >= r2.x && r1.x + r2.width <= r2.x + r2.width &&
                r1.y >= r2.y && r1.y + r1.height <= r2.y + r2.height);
    }

    public boolean intersectingWithScreenBoundaries(Rect head) {
        return (head.x < background.x || (head.x + head.width) > background.x + background.width ||
                head.y < background.y || (head.y + head.height) > background.y + background.height);
    }

    public void grow() {
        double newX = 0;
        double newY = 0;

        if (direction == RIGHT) {
            newX = body[tail].x - bodyHeight;
            newY = body[tail].y;
        } else if (direction == LEFT) {
            newX = body[tail].x + bodyWidth;
            newY = body[tail].y;
        } else if (direction == UP) {
            newX = body[tail].x;
            newY = body[tail].y + bodyHeight;
        } else if (direction == DOWN) {
            newX = body[tail].x;
            newY = body[tail].y - bodyHeight;
        }

        Rect newBodyPiece = new Rect(newX, newY, bodyWidth, bodyHeight);

        tail = (tail - 1) % body.length;
        body[tail] = newBodyPiece;
    }


    public void draw(Graphics2D g2) {
        //[1, 1, 1, 1, 1, 1, 0, 0, 0, 0]
        //[1, 1, 1, 1, 1, 1, 0, 0, 0, 1]

        for (int i = tail; i != head; i = (i + 1) % body.length) {
            Rect piece = body[i];
            double subWeight = (piece.width - 6.0) / 2.0;
            double subHeight = (piece.height - 6.0) / 2.0;

            g2.setColor(Color.black);
            g2.fill(new Rectangle2D.Double(piece.x + 2.0, piece.y + 2.0, subWeight, subHeight));
            g2.fill(new Rectangle2D.Double(piece.x + 4.0 + subWeight, piece.y + 2.0, subWeight, subHeight));
            g2.fill(new Rectangle2D.Double(piece.x + 2.0, piece.y + 4.0 + subHeight, subWeight, subHeight));
            g2.fill(new Rectangle2D.Double(piece.x + 4.0 + subWeight, piece.y + 4.0 + subHeight, subWeight, subHeight));
        }
    }
}
