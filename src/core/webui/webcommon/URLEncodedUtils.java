///**
// * Copyright The Apache Foundation
// * <p>
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * <p>
// * http://www.apache.org/licenses/LICENSE-2.0
// * <p>
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// * <p>
// * Code copied from decompiled code from the Apache HttpClient class of the same name and function.
// * I, Langdon Staab, did not write any of this code.
// *
// * @author The Apache Foundation
// */
//package core.webui.webcommon;
//
//
//
//
//
//
//
//
//
//import java.net.URI;
//import java.nio.ByteBuffer;
//import java.nio.CharBuffer;
//import java.nio.charset.Charset;
//import java.util.*;
//
//final class URLEncodedUtils {
//    private URLEncodedUtils(){
//        throw new InstantiationError("This class is uninstantiable.");
//    }
//    public static Map<String, String> parse(URI uri, Charset charset) {
//        Args.notNull(uri, "URI");
//        String query = uri.getRawQuery();
//        return query != null && !query.isBlank() ? parse(query, charset) : createEmptyList();
//    }
//
//    private static Map<String, String> parse(String s, Charset charset) {
//        if (s == null) {
//            return createEmptyList();
//        } else {
//            CharArrayBuffer buffer = new CharArrayBuffer(s.length());
//            buffer.append(s);
//            return parse(buffer, charset);
//        }
//    }
//
//    private static Map<String, String> parse(CharArrayBuffer buf, Charset charset) {
//        Args.notNull(buf, "Char array buffer");
//        TokenParser tokenParser = TokenParser.INSTANCE;
//        BitSet delimSet = new BitSet();
//
//        for (char separator : new char[]{'&', ';'}) {
//            delimSet.set(separator);
//        }
//
//        ParserCursor cursor = new ParserCursor(0, buf.length());
//        Map<String, String> list = new ArrayList<>();
//
//        while (!cursor.atEnd()) {
//            delimSet.set(61);
//            String name = tokenParser.parseToken(buf, cursor, delimSet);
//            String value = null;
//            if (!cursor.atEnd()) {
//                int delim = buf.charAt(cursor.getPos());
//                cursor.updatePos(cursor.getPos() + 1);
//                if (delim == 61) {
//                    delimSet.clear(61);
//                    value = tokenParser.parseValue(buf, cursor, delimSet);
//                    if (!cursor.atEnd()) {
//                        cursor.updatePos(cursor.getPos() + 1);
//                    }
//                }
//            }
//
//            if (!name.isBlank()) {
//                list.add(new BasicNameValuePair(decodeFormFields(name, charset), decodeFormFields(value, charset)));
//            }
//        }
//
//        return list;
//    }
//
//    private static String decodeFormFields(String content, Charset charset) {
//        return content == null ? null : urlDecode(content, charset != null ? charset : Consts.UTF_8);
//    }
//
//    private static String urlDecode(String content, Charset charset) {
//        if (content == null) {
//            return null;
//        } else {
//            ByteBuffer bb = ByteBuffer.allocate(content.length());
//            CharBuffer cb = CharBuffer.wrap(content);
//
//            while (cb.hasRemaining()) {
//                char c = cb.get();
//                if (c == '%' && cb.remaining() >= 2) {
//                    char uc = cb.get();
//                    char lc = cb.get();
//                    int u = Character.digit(uc, 16);
//                    int l = Character.digit(lc, 16);
//                    if (u != -1 && l != -1) {
//                        bb.put((byte) ((u << 4) + l));
//                    } else {
//                        bb.put((byte) 37);
//                        bb.put((byte) uc);
//                        bb.put((byte) lc);
//                    }
//                } else if (c == '+') {
//                    bb.put((byte) 32);
//                } else {
//                    bb.put((byte) c);
//                }
//            }
//
//            bb.flip();
//            return charset.decode(bb).toString();
//        }
//    }
//
//    private static Map<String, String> createEmptyList() {
//        return new HashMap<>();
//    }
//}