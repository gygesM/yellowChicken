package org.igeek.controller.collect;

import org.igeek.common.Const;
import org.igeek.common.ServerResponse;
import org.igeek.pojo.Organization;
import org.igeek.pojo.QualityCollection;
import org.igeek.pojo.QualityQuestion;
import org.igeek.service.IKilnService;
import org.igeek.service.IQualityCollectService;
import org.igeek.service.IQualityQuestionService;
import org.igeek.service.IRankService;
import org.igeek.vo.KilnVo;
import org.igeek.vo.RankVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.security.GeneralSecurityException;
import java.util.Set;

/**
 * Created by Gyges on 2017/6/29.
 * 质量采集的所有接口
 */
@Controller
@RequestMapping("/quality/collect")
public class QualityCollectController {

    public static final Logger logger = LoggerFactory.getLogger(QualityCollectController.class);

    @Autowired
    private IQualityCollectService iQualityCollectService;

    @Autowired
    private IKilnService iKilnService;

    @Autowired
    private IRankService iRankService;

    @Autowired
    private IQualityQuestionService iQualityQuestionService;

    /**
     * 主页面根据workerCode,进行过滤。
     *
     * @return
     */
    @RequestMapping("get_collect_homepage_info")
    @ResponseBody
    public ServerResponse getCollectHomePageInfo(@RequestParam(defaultValue = "") String workerCode, HttpSession session) {
        Organization organization = (Organization) session.getAttribute(Const.CURRENT_USER);
        if (organization == null) {
            return ServerResponse.createByErrorMsg("当前用户不存在");
        }
        if (workerCode.equals("empty") || workerCode.equals("")) {
            return iQualityCollectService.getCollectUserListWithFilter(workerCode, organization.getOrgId());
        } else {
            return iQualityCollectService.getCollectUserListWithFilter(workerCode, organization.getOrgId());
        }
    }


    /**
     * 获取工人产品列表
     *
     * @param status
     * @param workerId
     * @param session
     * @return
     */
    @RequestMapping("get_worker_productCode")
    @ResponseBody
    public ServerResponse getWorkerProductCode(@RequestParam(defaultValue = "1", required = false) Integer status,
                                               Integer workerId, HttpSession session) {
        Organization organization = (Organization) session.getAttribute(Const.CURRENT_USER);
        if (organization == null) {
            return ServerResponse.createByErrorMsg("当前用户不存在");
        }
        return iQualityCollectService.getWorkerProductCode(status, workerId, organization.getOrgId());
    }


    /**
     * 获取修改列表信息
     * @param workerId
     * @param session
     * @return
     */
    @RequestMapping(value = "get_collect_edit_infoList/{workerId}/{startTime}/{endTime}", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getCollectInfoDetail(@PathVariable Integer workerId,
                                               @PathVariable String startTime, @PathVariable String endTime,
                                               HttpSession session) {
        Organization organization = (Organization) session.getAttribute(Const.CURRENT_USER);
        if (organization == null) {
            return ServerResponse.createByErrorMsg("当前用户不存在");
        }
        return iQualityCollectService.getCollectInfoDetail(workerId, organization.getOrgId(), startTime, endTime);
    }


    /**
     * 获取单个修改信息
     *
     * @param workerId
     * @param collectId
     * @param session
     * @return
     */
    @RequestMapping(value = "get_collect_single_infoList/{workerId}/{collectId}", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getSingleCollectInfoDetail(@PathVariable Integer workerId,
                                                     @PathVariable String collectId, HttpSession session) {
        Organization organization = (Organization) session.getAttribute(Const.CURRENT_USER);
        if (organization == null) {
            return ServerResponse.createByErrorMsg("当前用户不存在");
        }
        return iQualityCollectService.getSingleCollectInfoDetail(workerId, collectId, organization.getOrgId());
    }


    /**
     * 更新和增加质量采集问题的员工信息、质量信息，等级、数量、
     * typeId 传参时将问题类型id,用, 逗号连接。也可以不输入
     * @param qualityCollection
     * @return
     */
    @RequestMapping("addOrUpdate")
    @ResponseBody
    public ServerResponse<String> addOrUpdateInfo(QualityCollection qualityCollection, HttpSession session) {
        Organization organization = (Organization) session.getAttribute(Const.CURRENT_USER);
        if (organization == null) {
            return ServerResponse.createByErrorMsg("当前用户不存在");
        }
        qualityCollection.setOrgId(organization.getOrgId());
        return iQualityCollectService.addOrUpdateInfo(qualityCollection);
    }


    /**
     * 增加或先修改问题信息
     * <p>
     * qualityId    质量问题id
     * quantity     数量
     * questionType 问题类型
     * coefficient  系数
     *
     * @return
     */
    @RequestMapping(value = "addOrUpdate_question", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> addOrUpdateQuestion(QualityQuestion qualityQuestion, HttpSession session) {
        Organization organization = (Organization) session.getAttribute(Const.CURRENT_USER);
        if (organization == null) {
            return ServerResponse.createByErrorMsg("当前用户不存在");
        }
        qualityQuestion.setOrgId(organization.getOrgId());
        return iQualityQuestionService.addOrUpdateQuestion(qualityQuestion);
    }

    /**
     * 删除信息。
     * @param workerId
     * @param collectId
     * @param session
     * @return
     */
    @RequestMapping(value = "delete_collect_single_info/{workerId}/{collectId}", method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> deleteCollectSingleInfo(@PathVariable Integer workerId,
                                                          @PathVariable String collectId,HttpSession session){
        Organization organization = (Organization) session.getAttribute(Const.CURRENT_USER);
        if (organization == null) {
            return ServerResponse.createByErrorMsg("当前用户不存在");
        }
        return iQualityQuestionService.deleteCollectSingleInfo(workerId,collectId,organization.getOrgId());
    }


    /**
     * 采集的用户列表
     * @param category
     * @param session
     * @return
     */
    @RequestMapping(value = "get_collect_userList/{category}", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getCollectUserList(@PathVariable Integer category,
                                             HttpSession session) {
        Organization organization = (Organization) session.getAttribute(Const.CURRENT_USER);
        if (organization == null) {
            return ServerResponse.createByErrorMsg("当前用户不存在");
        }
        return iQualityCollectService.getCollectUserList(category, organization.getOrgId());
    }


    /**
     * 获得窑炉信息列表
     *
     * @param status
     * @return 调通
     */
    @RequestMapping("get_kilnName_list")
    @ResponseBody
    public ServerResponse<Set<KilnVo>> getKilnList(@RequestParam(defaultValue = "1", required = false) Integer status,
                                                   HttpSession session) {

        Organization organization = (Organization) session.getAttribute(Const.CURRENT_USER);
        if (organization == null) {
            return ServerResponse.createByErrorMsg("当前用户不存在");
        }
        Integer orgId = organization.getOrgId();
        return iKilnService.searchKilnNameList(status, orgId);
    }


    /**
     * 获得等级标题
     *
     * @param status 已调通
     * @return
     */
    @RequestMapping(value = "get_rank_title")
    @ResponseBody
    public ServerResponse<Set<RankVo>> getRankTitle(@RequestParam(value = "status", defaultValue = "1") Integer status,
                                                    HttpSession session) {
        Organization organization = (Organization) session.getAttribute(Const.CURRENT_USER);
        if (organization == null) {
            return ServerResponse.createByErrorMsg("当前用户不存在");
        }
        return iRankService.searchRankTitle(status, organization.getOrgId());
    }


    /**
     * 获取工种信息列表
     *
     * @param category 输入员工类别（如：成型工=1）
     * @return 已经调通
     */
    @RequestMapping("get_user_category")
    @ResponseBody
    public ServerResponse getUserCategoryList(Integer category, HttpSession session) {
        Organization organization = (Organization) session.getAttribute(Const.CURRENT_USER);
        if (organization == null) {
            return ServerResponse.createByErrorMsg("当前用户不存在");
        }
        return iQualityCollectService.searchUserCategoryList(category, organization.getOrgId());
    }


    /**
     * 获取质量问题类别列表
     *
     * @param status              状态
     * @param questionCollectType 所属问题类型 1：成型问题，2：修坯 3：喷窑 4，登窑，5 烧窑。
     * @return 已调通
     */
    @RequestMapping("get_quality_category")
    @ResponseBody
    public ServerResponse getQualityCategoryList(@RequestParam(defaultValue = "1", required = false) Integer status,
                                                                 Integer questionCollectType,
                                                                 HttpSession session) {
        Organization organization = (Organization) session.getAttribute(Const.CURRENT_USER);
        if (organization == null) {
            return ServerResponse.createByErrorMsg("当前用户不存在");
        }
        return iQualityCollectService.getQualityCategoryList(status, questionCollectType, organization.getOrgId());
    }


    /**
     * 获取质量采集信息详情
     *
     * @param session
     * @return
     */
    @RequestMapping(value = "get_quality_collect_detail/{workerId}/{collectId}", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse getQualityCollectDetail(@PathVariable Integer workerId,
                                                  @PathVariable String collectId, HttpSession session) {
        try {
            Organization organization = (Organization) session.getAttribute(Const.CURRENT_USER);
            if (organization == null) {
                return ServerResponse.createByErrorMsg("当前用户不存在");
            }
            return iQualityCollectService.getQualityCollectDetail(organization.getOrgId(), workerId, collectId);
        } catch (GeneralSecurityException e) {
            logger.error("获取信息异常", e);
        }
        return ServerResponse.createByErrorMsg("获取质量采集详情信息异常");
    }


}
