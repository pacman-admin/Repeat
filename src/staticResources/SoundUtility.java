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
package staticResources;

import javax.sound.sampled.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to play short sounds.
 */
public class SoundUtility {

    private static final Logger LOGGER = Logger.getLogger(SoundUtility.class.getName());

    private SoundUtility() {
    }

    /**
     * Play a built in sound.
     */
    public static void play(BuiltInSound sound) {
        try {
            play(BootStrapResources.getStaticContentStream(sound.value));
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
            LOGGER.log(Level.WARNING, "Encounter exception when playing sound " + sound + ".", e);
        }
    }

    /**
     * Play a sound from a file path.
     * Only support wav files.
     * <p>
     * This is only meant to play very short sound files.
     *
     * @param path path to the sound file.
     * @throws LineUnavailableException
     * @throws IOException
     * @throws UnsupportedAudioFileException
     * @throws FileNotFoundException
     * @throws InterruptedException
     */
    public static void playShortSound(String path) {
        File f = new File(path);
        try {
            play(new FileInputStream(f));
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
            LOGGER.log(Level.WARNING, "Encounter exception when playing sound at " + path + ".", e);
        }
    }

    /**
     * Play a sound from an input stream.
     * Only support wav files.
     * <p>
     * This is only meant to play very short sound files.
     *
     * @param stream stream to the sound file.
     * @throws UnsupportedAudioFileException
     * @throws IOException
     * @throws LineUnavailableException
     * @throws InterruptedException
     */
    private static void play(InputStream stream) throws UnsupportedAudioFileException, IOException, LineUnavailableException, InterruptedException {
        AudioInputStream ais = AudioSystem.getAudioInputStream(new BufferedInputStream(stream));
        Clip clip = AudioSystem.getClip();
        clip.open(ais);
        clip.start();
        while (!clip.isRunning()) {
        }
        clip.drain();
        clip.close();
    }

    public enum BuiltInSound {
        POSITIVE1(BootStrapResources.SOUND_POSITIVE1_PATH), POSITIVE2(BootStrapResources.SOUND_POSITIVE2_PATH), POSITIVE3(BootStrapResources.SOUND_POSITIVE3_PATH), POSITIVE4(BootStrapResources.SOUND_POSITIVE4_PATH), NEGATIVE1(BootStrapResources.SOUND_NEGATIVE1_PATH), NEGATIVE2(BootStrapResources.SOUND_NEGATIVE2_PATH),
        ;

        private final String value;

        BuiltInSound(String value) {
            this.value = value;
        }
    }
}
