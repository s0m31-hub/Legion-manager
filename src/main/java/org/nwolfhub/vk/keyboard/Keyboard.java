package org.nwolfhub.vk.keyboard;

import java.util.List;

public class Keyboard {

    public Keyboard(boolean one_time, int sizey, int sizex) {
        this.one_time = one_time;
        this.inline=false;
        keyboardButtons = new KeyboardButton[sizey][sizex];
    }

    public static enum Color {
        primary,
        secondary,
        positive,
        negative
    }

    public boolean one_time;

    public Keyboard setInline(boolean inline) {
        this.inline = inline;
        return this;
    }

    private boolean inline;
    public KeyboardButton[][] keyboardButtons;

    public Keyboard addButton(KeyboardButton button, int posy, int posx) {
        keyboardButtons[posy][posx] = button;
        return this;
    }

    @Override
    public String toString() {
        char s = '"';
        StringBuilder builder = new StringBuilder();
        if(!inline) {
            builder.append("{\"one_time\":").append(one_time);
        } else {
            builder.append("{\"inline\": ").append(true);
        }
        builder.append(",\"buttons\":[");
        for (int y = 0; y < keyboardButtons.length; y++) {
            builder.append("[");
            for (int x = 0; x < keyboardButtons[0].length; x++) {
                builder.append("{");
                builder.append(keyboardButtons[y][x].toString());
                builder.append("}");
                if (x + 1 != keyboardButtons[0].length) {
                    builder.append(",");
                }
            }
            builder.append("]");
            if (y + 1 != keyboardButtons.length) {
                builder.append(",");
            }
        }
        builder.append("]}");
        return builder.toString();
    }

    private static class KeyboardButton {
        public KeyboardButton(String type, String color) {
            this.type = type;
            this.color = color;
        }

        public final String type;
        public String color;
    }

    public static class TextButton extends KeyboardButton {
        public String getLabel() {
            return label;
        }

        public String getPayload() {
            return payload;
        }

        public TextButton setColor(String color) {
            this.color = color;
            return this;
        }
        public TextButton setColor(Color color) {
            this.color = color.name();
            return this;
        }


        public TextButton setPayload(String payload) {
            this.payload = payload;
            return this;
        }

        public TextButton setLabel(String label) {
            this.label = label;
            return this;
        }

        public String label;
        public String payload;

        public TextButton() {
            super("text", "primary");
        }
        public TextButton(String label, String payload) {
            super("text", "primary");
            this.label = label;
            this.payload = payload;
        }

        @Override
        public String toString() {
            if (payload != null) {
                return "\"action\":{\"type\":\"text\",\"label\":\"" + label + "\",\"payload\":" + payload + "},\"color\":\"" + color + "\"";
            } else {
                return "\"action\":{\"type\":\"text\",\"label\":\"" + label + "\"},\"color\":\"" + color + "\"}";
            }
        }
    }

    public static class CallbackButton extends KeyboardButton {
        public String getLabel() {
            return label;
        }

        public String getPayload() {
            return payload;
        }

        public CallbackButton setColor(String color) {
            this.color = color;
            return this;
        }

        public CallbackButton setPayload(String payload) {
            this.payload = payload;
            return this;
        }

        public CallbackButton setLabel(String label) {
            this.label = label;
            return this;
        }

        public String label;
        public String payload;

        public CallbackButton() {
            super("text", "primary");
        }

        public CallbackButton(String label, String payload) {
            super("callback", "primary");
            this.label = label;
            this.payload = payload;
        }

        @Override
        public String toString() {
            return "\"action\":{\"type\":\"callback\",\"label\":\"" + label + "\",\"payload\":" + payload + "},\"color\":\"" + color + "\"";
        }

    }
}
