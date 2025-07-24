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
package commonTools;

import core.languageHandler.Language;

public class AreaClickerTool extends RepeatTool {

    public AreaClickerTool() {
        super();
        imports.add("import java.awt.Point;");
        imports.add("import utilities.Function;");
        imports.add("import core.Core;");
    }

    @Override
    protected boolean isSupported(Language language) {
        return language == Language.JAVA;
    }

    @Override
    protected String getBodySource(Language language) {
        if (language == Language.JAVA) {

            return TWO_TAB + "Point topLeft = new Point(0,0);\n" + TWO_TAB + "Point bottomRight = new Point(20,20);\n" + TWO_TAB + "int row = 5;\n" + TWO_TAB + "int column = 5;\n" + TWO_TAB + "controller.mouse().moveArea(topLeft, bottomRight, row, column, new Function<Point, Void>() {\n" + THREE_TAB + "public Void apply(Point r) {\n" + FOUR_TAB + "System.out.println(\"At \" + r.x + \", \" + r.y);\n" + FOUR_TAB + "return null;\n" + THREE_TAB + "}\n" + TWO_TAB + "});\n";
        }

        return null;
    }
}
