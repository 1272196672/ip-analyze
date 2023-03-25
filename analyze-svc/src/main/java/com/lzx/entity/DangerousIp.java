package com.lzx.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author Bobby.zx.lin
 * @since 2023-03-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DangerousIp extends Model<DangerousIp> {

    private static final long serialVersionUID = 1L;

    @TableId
    private String ip;

    private Boolean stateFrequent;

    private Boolean stateNum;

    private Boolean stateGeo;

    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.ip;
    }

}
