package org.igeek.controller.quality;

import com.github.pagehelper.PageInfo;
import org.igeek.common.ServerResponse;
import org.igeek.pojo.Kiln;
import org.igeek.service.IKilnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Gyges on 2017/6/27.
 * 窑炉信息
 */
@Controller
@RequestMapping(value = "/kiln/")
public class KilnController {

    @Autowired
    private IKilnService ikilnService;


    /**
     * 增加更新窑炉信息
     *
     * @param kiln
     * @return
     */
    @RequestMapping(value = "addOrUpdate", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> updateOrSaveKilnValue(Kiln kiln) {
        return ikilnService.updateOrSaveKilnValue(kiln);
    }

    /**
     * 列出所有窑炉信息
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "get_list", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo> listAllKiln(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                                @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return ikilnService.listAllKiln(pageNum, pageSize);
    }


    /**
     * 更新状态值
     *
     * @param kilnId
     * @param status
     * @return
     */
    @RequestMapping(value = "update_status")
    @ResponseBody
    public ServerResponse<String> updateStatus(Integer kilnId, String status) {
        return ikilnService.updateStatus(kilnId, status);
    }

}
