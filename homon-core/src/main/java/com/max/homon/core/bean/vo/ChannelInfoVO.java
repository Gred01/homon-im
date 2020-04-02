package com.max.homon.core.bean.vo;

import com.max.homon.core.bean.base.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ChannelInfoVO extends BaseVO {

    private String host;
    private Integer post;
}
