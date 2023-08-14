package org.opentcs.modeleditor.util;

import org.opentcs.access.to.model.BlockCreationTO;
import org.opentcs.data.model.Block;
import org.opentcs.guing.base.model.elements.BlockModel;
import org.opentcs.guing.base.model.elements.PathModel;
import org.opentcs.guing.base.model.elements.PointModel;
import org.opentcs.guing.common.model.SystemModel;
import org.opentcs.util.heliutil.HeLiResourceUtil;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @PACKAGE_NAME: org.opentcs.modeleditor.util
 * @NAME: AutoCreateHeLiBlock
 * @USER: FSY
 * @DATE: 2023/8/11 0011
 * @TIME: 16:56
 * @YEAR: 2023
 * @MONTH_NAME_SHORT: 8月
 * @DAY_NAME_SHORT: 周五
 * @PROJECT_NAME: openTCS
 * @Description:
 */
public class AutoCreateHeLiBlock {
  private final SystemModel systemModel;
  private final List<PathModel> pathModels;
  private final List<PointModel> pointModels;
  private final List<BlockCreationTO> blockCreationTOs = new ArrayList<>();
  private final Map<String,Set<String >>haveDoublePathPoints = new HashMap<>();
  private final String preName = "HeLiBlock-";
  public AutoCreateHeLiBlock(SystemModel systemModel)
  {
    this.systemModel = systemModel;
    this.pathModels = systemModel.getPathModels();
    this.pointModels = systemModel.getPointModels();
  }
  public void createHeLiBlocks()
  {
    //todo:进行自动创建block
    /**
     * 1：寻找出所有连接了双向节点的数据
     * 2：递归判断双向节点所组成的block points
     * 3：创建BlockCreationTO对象
     */
    long startTime = System.currentTimeMillis();
    findAllDoublePathPoint();
    Set<String> visitedPoint  = new HashSet<String>();
    Map<String, Set<String>> pointLinkDoubleDes = new HashMap<>();
    Set<Set<String>> blockPathNames = new HashSet<>();
    //1:寻找block双向节点
    for (PointModel pointModel : pointModels) {
      Set<String> blockPoints = findBlockPoints(pointModel.getName(),visitedPoint,pointLinkDoubleDes);
      if (blockPoints != null)
        blockPathNames.add(blockPoints);
    }
    Set<BlockModel> heLiBlocks = systemModel.getBlockModels().stream().filter(blockModel -> blockModel.getName().contains(preName)).collect(Collectors.toSet());
    List<BlockModel> allBlockModels = systemModel.getBlockModels();
    int num =heLiBlocks.size()+1;
    for (Set<String> blockPathName : blockPathNames) {
      Set<String> blockSource = createBlockSource(blockPathName, pointLinkDoubleDes);
      //如果先前已经生成了block包含了这次block中的所有的元素那么就不会生成这个block
      if (allBlockModels.stream().anyMatch(blockModel -> blockModel.getPropertyElements().getItems().containsAll(blockSource))) {
        continue;
      }
      BlockCreationTO blockCreationTo = createBlockCreationTo(blockSource,num);
      blockCreationTOs.add(blockCreationTo);
      num++;
    }
    System.out.println("自动block耗时："+(System.currentTimeMillis()-startTime)+" ms");
  }

  private Set<String> createBlockSource(Set<String> blockPoints, Map<String, Set<String>> pointLinkDoubleDes) {
    Set<String> result = new HashSet<>();
    //这是一个block当中包含的点资源信息
    for (String blockPoint : blockPoints) {
      Set<String> linkDoubleDesPointNames = pointLinkDoubleDes.get(blockPoint);
      for (String linkDoubleDesPointName : linkDoubleDesPointNames) {
        String pathName = HeLiResourceUtil.createPathName(blockPoint, linkDoubleDesPointName);
        result.add(blockPoint);
        result.add(pathName);
      }
    }
    return result;
  }

  /**
   * 寻找出所有的双向路径的站点
   */
  private void findAllDoublePathPoint() {
    Map<String ,Set<String >>varPath = new HashMap<>();
    for (PathModel pathModel : pathModels) {
      String pathName = pathModel.getName();
      String sourcePNameFromPathName = HeLiResourceUtil.getSourcePNameFromPathName(pathName);
      String desPNameFromPathName = HeLiResourceUtil.getDesPNameFromPathName(pathName);
      if (varPath.computeIfAbsent(desPNameFromPathName, v->new HashSet<>()).contains(sourcePNameFromPathName)) {
        haveDoublePathPoints.computeIfAbsent(sourcePNameFromPathName,v->new HashSet<>()).add(desPNameFromPathName);
        haveDoublePathPoints.computeIfAbsent(desPNameFromPathName,v->new HashSet<>()).add(sourcePNameFromPathName);
      }else
      {
        varPath.computeIfAbsent(sourcePNameFromPathName,v->new HashSet<>()).add(desPNameFromPathName);
      }
    }
  }

  /**
   * 寻找一个站点连接的所有双向路径的站点位置
   * @param sourcePointName
   * @param visits
   * @param pointLinkDoubleDes
   * @return
   */
  private Set<String> findBlockPoints(String  sourcePointName, Set<String> visits, Map<String, Set<String>> pointLinkDoubleDes) {
    if (visits.contains(sourcePointName)||!haveDoublePathPoints.containsKey(sourcePointName))
      return null;
    visits.add(sourcePointName);
    Set<String> strings = haveDoublePathPoints.get(sourcePointName);
    //把连接的双向数据放进去
    pointLinkDoubleDes.computeIfAbsent(sourcePointName, v -> new HashSet<String>(strings));
    Set<String> result = new HashSet<>(strings);
    for (String string : strings) {
      Set<String> blockPoints = findBlockPoints(string, visits, pointLinkDoubleDes);
      if (blockPoints != null)
        result.addAll(blockPoints);
    }
    return result;
  }

  private BlockCreationTO createBlockCreationTo(Set<String> blockPathName, int num)
  {
    Random random = new Random();
    int red = random.nextInt(256);
    int green = random.nextInt(256);
    int blue = random.nextInt(256);
    Color color = new Color(red, green, blue);

    return new BlockCreationTO(preName + num)
        .withMemberNames(blockPathName)
        .withType(Block.Type.SINGLE_VEHICLE_ONLY)
        .withLayout(new BlockCreationTO.Layout(color));
  }

  public  List<BlockCreationTO> getBlockCreationTOs()
  {
    return this.blockCreationTOs;
  }

}
