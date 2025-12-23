package cn.wubo.sql.forge.enums;

import cn.wubo.sql.forge.jdbc.SafeAppendable;

public enum LimitingRowsStrategy {
    NOP {
        @Override
        public void appendClause(SafeAppendable builder, String offset, String limit) {
        }
    },
    ISO {
        @Override
        public void appendClause(SafeAppendable builder, String offset, String limit) {
            if (offset != null) {
                builder.append(" OFFSET ").append(offset).append(" ROWS");
            }
            if (limit != null) {
                builder.append(" FETCH FIRST ").append(limit).append(" ROWS ONLY");
            }
        }
    },
    OFFSET_LIMIT {
        @Override
        public void appendClause(SafeAppendable builder, String offset, String limit) {
            if (limit != null) {
                builder.append(" LIMIT ").append(limit);
            }
            if (offset != null) {
                builder.append(" OFFSET ").append(offset);
            }
        }
    };

    public abstract void appendClause(SafeAppendable builder, String offset, String limit);
}
