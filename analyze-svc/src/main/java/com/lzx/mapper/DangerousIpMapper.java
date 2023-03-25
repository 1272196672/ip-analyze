package com.lzx.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.lzx.entity.DangerousIp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzx.util.EsUtils;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Bobby.zx.lin
 * @since 2023-03-08
 */
@Mapper
public interface DangerousIpMapper extends BaseMapper<DangerousIp> {
}
