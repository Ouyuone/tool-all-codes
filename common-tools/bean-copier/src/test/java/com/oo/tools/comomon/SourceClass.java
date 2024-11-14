package com.oo.tools.comomon;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/11/14 14:50:12
 */
public class SourceClass {
    
    private int id;
    
    private String name;
    
    private Boolean isDelete;
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    
    public Boolean getIsDelete() {
        return isDelete;
    }
    
    public void setIsDelete(Boolean delete) {
        isDelete = delete;
    }
    
    @Override
    public String toString() {
        return "SourceClass{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isDelete=" + isDelete +
                '}';
    }
}
