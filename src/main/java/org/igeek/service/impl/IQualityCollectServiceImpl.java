package org.igeek.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.igeek.common.ResponseCode;
import org.igeek.common.ServerResponse;
import org.igeek.dao.*;
import org.igeek.exception.GeneralServiceException;
import org.igeek.pojo.*;
import org.igeek.service.IQualityCollectService;
import org.igeek.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Created by Gyges on 2017/6/29.
 */
@Service(value = "iQualityCollectService")
public class IQualityCollectServiceImpl implements IQualityCollectService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QualityCollectionMapper collectionMapper;
    @Autowired
    private QualityMapper qualityMapper;
    @Autowired
    private SpCollectMapper spCollectMapper;
    @Autowired
    private KilnMapper kilnMapper;
    @Autowired
    private RankMapper rankMapper;
    @Autowired
    private QualityQuestionMapper qualityQuestionMapper;


    /**
     * 更新增加
     *
     * @param qualityCollection
     * @return
     */
    @Override
    public ServerResponse<String> addOrUpdateInfo(QualityCollection qualityCollection) {
        if (qualityCollection.getId() == null) {
            String workerCode = qualityCollection.getUserCode();
            qualityCollection.setCount(0);
            qualityCollection.setStatus(1);
            List<QualityCollection> qualityCollections = collectionMapper.getQualityCollection(workerCode, qualityCollection.getUserId(), qualityCollection.getOrgId());
            if (qualityCollections.size() > 0) {
                return ServerResponse.createByErrorMsg("该人员的质量信息已经被采集");
            }
            int resultCount = collectionMapper.insert(qualityCollection);
            if (resultCount > 0) {
                return ServerResponse.createBySuccess("插入质量采集问题信息成功");
            }
            return ServerResponse.createBySuccess("插入质量采集问题信息失败");
        } else {
            int rowCount = collectionMapper.updateByPrimaryKey(qualityCollection);
            if (rowCount > 0) {
                return ServerResponse.createBySuccess("更新质量采集问题成功");
            }
            return ServerResponse.createBySuccess("更新质量采集问题失败");
        }
    }


    /**
     * 1 扣系数 还是扣钱(1:扣款问题 2.扣系数问题)
     * 2.更新问题列表
     * 3.选择工种类型
     *
     * @param
     * @return
     */


    public ServerResponse<Set<UserVo>> searchUserList(String name, Integer orgId) {
        Set<UserVo> userVoList = Sets.newHashSet();
        if (name != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("%").append(name).append("%");
            name = sb.toString();
        }
        List<User> userList = userMapper.getUserList(name, orgId);
        if (userList.size() > 0) {
            for (User user : userList) {
                UserVo userVo = new UserVo();
                userVo.setUserNameCode(user.getNumstr() + "-" + user.getName());
                userVoList.add(userVo);
            }
            return ServerResponse.createBySuccess(userVoList);
        }
        return ServerResponse.createByErrorMsg("无法获得该姓名列表");
    }


    @Override
    public ServerResponse<Set<UserVo>> searchUserCategoryList(Integer category, Integer orgId) {
        Set<UserVo> userVoList = Sets.newHashSet();
        if (category != null) {
            List<User> userList = userMapper.getUserCategoryList(category, orgId);
            if (userList.size() > 0) {
                for (User user : userList) {
                    UserVo userVo = new UserVo();
                    userVo.setUserNameCode(user.getNumstr() + "-" + user.getName());
                    userVoList.add(userVo);
                }
                return ServerResponse.createBySuccess("获取工人列表成功", userVoList);
            }
        }
        return ServerResponse.createByErrorMsg("获取工人列表失败");
    }


    public ServerResponse<Set<ProductCollectVo>> searchProIdList(Integer status, Integer workerId, Integer orgId) {
// TODO: 2017/6/30 查出产品代号，而且是成型工的.
        List<SpCollect> productList = spCollectMapper.getSpCollectList(status, workerId, orgId);
        Set<ProductCollectVo> ProductCollectVoSet = Sets.newHashSet();
        if (productList.size() > 0) {
            for (SpCollect spCollect : productList) {
                ProductCollectVo productCollectVo = new ProductCollectVo();
                productCollectVo.setProductDetail(spCollect.getProCode());
                productCollectVo.setWorkerName(spCollect.getUserName());
                productCollectVo.setWorkerId(spCollect.getUserId());
                productCollectVo.setWorkerCode(spCollect.getUserCode());
                productCollectVo.setCount(0);//默认为零
                ProductCollectVoSet.add(productCollectVo);
            }
            return ServerResponse.createBySuccess(ProductCollectVoSet);
        }
        return ServerResponse.createByErrorMsg("查询成型工对应产品列表失败");
    }


    public ServerResponse<Set<QualityVo>> getQualityCategoryList(Integer status, Integer questionCollectType, Integer orgId) {
        List<Quality> qualityList = qualityMapper.selectAllQualityQuestion(status, questionCollectType, orgId);
        Set<QualityVo> qualityVoList = Sets.newHashSet();
        if (qualityList.size() > 0) {
            for (Quality quality : qualityList) {
                QualityVo qualityVo = new QualityVo();
                qualityVo.setQualityIdName(quality.getTitle());
                qualityVo.setCollectType(quality.getQuestionType());
                qualityVo.setQualityId(quality.getId());
                qualityVo.setWorkerId(quality.getUserId());
                qualityVoList.add(qualityVo);
            }
            return ServerResponse.createBySuccess("获取质量问题列表成功", qualityVoList);
        }
        return ServerResponse.createByErrorMsg("获取质量问题列表失败");
    }


    @Override
    public ServerResponse<List<QualityCollectVo>> getQualityCollectInfo(String workerCode, Integer workerId, Integer orgId) {
        List<QualityCollection> qualityCollectionList = null;
        if (workerCode.equals("empty")) {
            qualityCollectionList = collectionMapper.getQualityCollectionWithEmpty(orgId);
            List<QualityCollectVo> qualityCollectVoList = Lists.newArrayList();
            if (qualityCollectionList.size() > 0) {
                for (QualityCollection collection : qualityCollectionList) {
                    QualityCollectVo qualityCollectVo = assembleQualityInfo(collection);
                    qualityCollectVoList.add(qualityCollectVo);
                }
                return ServerResponse.createBySuccess(qualityCollectVoList);
            }
            return ServerResponse.createByErrorMsg("获取质量采集列表信息失败");
        }
        qualityCollectionList = collectionMapper.getQualityCollection(workerCode, workerId, orgId);
        List<QualityCollectVo> qualityCollectVoList = Lists.newArrayList();
        if (qualityCollectionList.size() > 0) {
            for (QualityCollection collection : qualityCollectionList) {
                QualityCollectVo qualityCollectVo = assembleQualityInfo(collection);
                qualityCollectVoList.add(qualityCollectVo);
            }
            return ServerResponse.createBySuccess(qualityCollectVoList);
        }
        return ServerResponse.createByErrorMsg("获取质量采集列表信息失败");
    }


    /**
     * 组装数据
     *
     * @param qualityCollection
     * @return
     */
    private QualityCollectVo assembleQualityInfo(QualityCollection qualityCollection) {
        QualityCollectVo qualityCollectVo = new QualityCollectVo();
        qualityCollectVo.setCollectId(qualityCollection.getId());
        qualityCollectVo.setFormWorkerName(qualityCollection.getUserName());
        qualityCollectVo.setFormWorkerNum(qualityCollection.getUserCode());
        qualityCollectVo.setCount(qualityCollection.getCount());
        return qualityCollectVo;
    }


    public ServerResponse<String> updateCount(Integer collectId, Integer workerId, Long count, Integer orgId) {
        if (collectId != null) {
            int rowCount = collectionMapper.updateCollectCount(collectId, workerId, count, orgId);
            if (rowCount > 0) {
                return ServerResponse.createBySuccess("更新采集次数成功");
            }
            return ServerResponse.createByErrorMsg("更新采集次数失败");
        }
        return ServerResponse.createByErrorCodeAndMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getCodeDesc());
    }

//    public ServerResponse searchAllCollectList(Integer orgId){
//        List<QualityCollection>  qualityCollectionList = collectionMapper.getAllCollectionList(orgId);
//        if (CollectionUtils.isEmpty(qualityCollectionList)){
//            return ServerResponse.createByErrorMsg("质量采集列表为空");
//        }
//        return ServerResponse.createBySuccess(qualityCollectionList);
//    }


    @Override
    public ServerResponse getQualityCollectDetail(Integer orgId, Integer workerId) throws GeneralSecurityException {
        if (workerId == null) {
            return ServerResponse.createByErrorCodeAndMsg(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getCodeDesc());
        }
        CollectDetail collectDetail = null;
        List<QualityCollection> qualityCollectionList = collectionMapper.getAllCollectionList(orgId, workerId);
        if (CollectionUtils.isNotEmpty(qualityCollectionList)) {
            for (QualityCollection qualityCollection : qualityCollectionList) {
                collectDetail = assembleCollectDetail(qualityCollection);
            }
            return ServerResponse.createBySuccess(collectDetail);
        }
        return ServerResponse.createByErrorMsg("获取质量采集信息详情失败");
    }

    /**
     * 组装数据
     *
     * @param collection
     * @return
     */
    private CollectDetail assembleCollectDetail(QualityCollection collection) throws GeneralSecurityException {
        CollectDetail collectDetail = new CollectDetail();
        collectDetail.setId(collection.getId());//用于修改数据
        Kiln kiln = kilnMapper.selectByPrimaryKey(collection.getYaoluId());
        if (Objects.isNull(kiln)) {
            throw new GeneralServiceException("窑炉信息不存在");
        }
        collectDetail.setKilnName(kiln.getTitle());
        SpCollect spCollect = spCollectMapper.selectSpCollectByProductId(collection.getProductId(), collection.getOrgId());
        if (Objects.isNull(spCollect)) {
            throw new GeneralServiceException("收坯采集的产品不存在");
        }
        collectDetail.setProductCode(spCollect.getProCode());
        collectDetail.setQuantity(collection.getQuantity());
        Rank rank = rankMapper.selectByPrimaryKey(collection.getRankId());
        if (Objects.isNull(rank)) {
            throw new GeneralServiceException("等级信息不存在");
        }
        collectDetail.setRankName(rank.getTitle());
        collectDetail.setWorkerName(collection.getUserName());
        collectDetail.setWorkerId(collection.getUserId());
        List<QualityTypeVo> qualityTypeVoList = Lists.newArrayList();
        List<QualityQuestion> qualityQuestionList = qualityQuestionMapper.getQualityQuestionList(null, collection.getUserId(), collection.getOrgId());
        if (CollectionUtils.isEmpty(qualityQuestionList)) {
            throw new GeneralServiceException("质量问题信息不存在");
        }
        for (QualityQuestion qualityQuestion : qualityQuestionList) {
            QualityTypeVo qualityTypeVo = new QualityTypeVo();
            qualityTypeVo.setId(qualityQuestion.getId());//用于修改数据
            qualityTypeVo.setCoefficient(qualityQuestion.getCoefficient());
            qualityTypeVo.setCollectType(qualityQuestion.getCollectType());
            qualityTypeVo.setCollectId(qualityQuestion.getCollectId());
            qualityTypeVo.setQuestionId(qualityQuestion.getQuestionId());
            qualityTypeVo.setQuestionName(qualityQuestion.getQuestionName());
            qualityTypeVo.setQuestionWorkerName(qualityQuestion.getWorkName());
            qualityTypeVo.setQuestionQuantity(qualityQuestion.getQuantity());
            qualityTypeVoList.add(qualityTypeVo);
        }
        collectDetail.setQualityTypeVoList(qualityTypeVoList);
        return collectDetail;
    }

    /**
     * collectType
     * workerId
     */


}
