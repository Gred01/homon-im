package com.max.homon.core.bean.vo;

import com.max.homon.core.bean.base.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class ChannelInfoVO extends BaseVO {

    private String host;
    private Integer post;
}
