package com.oo.tools.spring.boot.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AbsEntity</p>
 *
 * @author Yu.ou
 * @desc:
 * @since: 1.0.0
 */
@Data
public abstract class AbsEntity implements Id, Created, Updated {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField(value = "created_by", fill = FieldFill.INSERT)
    private Long createdBy;
    
    @TableField(value = "updated_by", fill = FieldFill.UPDATE)
    private Long updatedBy;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    @TableLogic(value = "0", delval = "1")
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Boolean deleted;
}
