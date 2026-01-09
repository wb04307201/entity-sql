package cn.wubo.sql.forge.jdbc;

import lombok.Getter;

import java.io.IOException;

public class SafeAppendable {
    private final Appendable appendable;
    @Getter
    private boolean empty = true;

    public SafeAppendable(Appendable a) {
        this.appendable = a;
    }

    public SafeAppendable append(CharSequence s) {
        try {
            if (empty && !s.isEmpty()) {
                empty = false;
            }
            appendable.append(s);
        } catch (IOException e) {
            throw new SqlGenerationException(e);
        }
        return this;
    }
}
