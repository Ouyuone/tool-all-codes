package com.oo.tools.comomon;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/11/14 14:50:34
 */
public class TargetClass {
    
    private int id;
    
    @FieldMapping(source = "name", targetClass = SourceClass.class)
    private String nickName;

    private String isDelete;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNickName() {
        return nickName;
    }
    
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    
    @Override
    public String toString() {
        return "TargetClass{" +
                "id=" + id +
                ", nickName='" + nickName + '\'' +
                ", isDelete='" + isDelete + '\'' +
                '}';
    }
    
    public String getIsDelete() {
        return isDelete;
    }
    
    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }
}
