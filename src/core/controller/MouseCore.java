/**
 * Copyright 2025 Langdon Staab
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Langdon Staab
 * @author HP Truong
 */
package core.controller;

import core.controller.internals.AbstractMouseCoreImplementation;
import utilities.Function;

import java.awt.*;

public class MouseCore extends AbstractMouseCoreImplementation {

    private AbstractMouseCoreImplementation m;

    MouseCore(AbstractMouseCoreImplementation m) {
        this.m = m;
    }

    @Override
    public Point getPosition() {
        return m.getPosition();
    }

    @Override
    public Color getColor(int x, int y) {
        return m.getColor(x, y);
    }

    @Override
    public final Color getColor() {
        return m.getColor();
    }

    @Override
    public void hold(int mask, int duration) throws InterruptedException {
        m.hold(mask, duration);
    }

    @Override
    public void hold(int mask, int x, int y, int duration) throws InterruptedException {
        m.hold(mask, x, y, duration);
    }

    @Override
    public void press(int mask) {
        m.press(mask);
    }

    @Override
    public void release(int mask) {
        m.release(mask);
    }

    @Override
    public void move(int newX, int newY) {
        m.move(newX, newY);
    }

    @Override
    public void drag(int sourceX, int sourceY, int destX, int destY) {
        m.drag(sourceX, sourceY, destX, destY);
    }

    @Override
    public void moveBy(int amountX, int amountY) {
        m.moveBy(amountX, amountY);
    }

    @Override
    public void dragBy(int amountX, int amountY) {
        m.dragBy(amountX, amountY);
    }

    /**
     * Not fully supported. Use at own risk
     * Move mouse in a grid (defined by topLeft, bottomRight, number of column and number of row)
     * and perform an action at each point on the grid
     *
     * @param topLeft     topLeft coordinate of grid
     * @param bottomRight bottomRight coordinate of grid
     * @param col         number of column of grid
     * @param row         number of row of grid
     * @param action      action to perform.
     */
    public void moveArea(Point topLeft, Point bottomRight, int col, int row, Function<Point, Void> action) {
        if (col < 1 || row < 1) {
            return;
        }

        Point current = new Point(topLeft.x, topLeft.y);

        int xIncrement = (bottomRight.x - topLeft.x) / col;
        int yIncrement = (bottomRight.y - topLeft.y) / row;

        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                move(current);
                action.apply(current);

                current.x += xIncrement;

                if (current.x > bottomRight.x) {
                    current.x = topLeft.x;
                    current.y += yIncrement;
                }
            }
        }
    }
}
