export const exampleTemplate = `SELECT * FROM users WHERE 1=1
<if test="name != null && name != ''">AND username = #{name}</if>
<if test="ids != null && !ids.isEmpty()"><foreach collection="ids" item="id" open="AND id IN (" separator="," close=")">#{id}</foreach></if>
<if test="(name == null || name == '') && (ids == null || ids.isEmpty()) ">AND 0=1</if>
ORDER BY username DESC`;