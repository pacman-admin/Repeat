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
package core.cli;

public enum CliExitCodes {
    // Exit codes 1 - 2, 126 - 165, and 255 are reserved.
    INVALID_ARGUMENTS(3), UNKNOWN_MODULE(4), UNKNOWN_ACTION(5), IO_EXCEPTION(6);

    private final int code;

    CliExitCodes(int code) {
        this.code = code;
    }

    public void exit() {
        System.exit(code);
    }

    public int getCode() {
        return code;
    }
}
