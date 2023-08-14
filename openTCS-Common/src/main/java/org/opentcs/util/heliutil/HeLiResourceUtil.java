package org.opentcs.util.heliutil;

/**
 * @PACKAGE_NAME: org.opentcs.util.heliutil
 * @NAME: HeLiResourceUtil
 * @USER: FSY
 * @DATE: 2023/8/1 0001
 * @TIME: 11:49
 * @YEAR: 2023
 * @MONTH_NAME_SHORT: 8月
 * @DAY_NAME_SHORT: 周二
 * @PROJECT_NAME: openTCS
 * @Description:
 */
public class HeLiResourceUtil {

  public static String pathSplit = " --- ";

  public static String createConvertPathName(String pathName)
  {
    if(pathName==null)
      return null;
    String[] split = pathName.split(pathSplit);
    if(split.length!=2)
      return null;
    return split[1]+pathSplit+split[0];
  }

  public static String getSourcePNameFromPathName(String pathName)
  {
    if(pathName==null)
      return null;
    String[] split = pathName.split(pathSplit);
    if(split.length!=2)
      return null;
    return split[0];
  }
  public static String getDesPNameFromPathName(String pathName)
  {
    if(pathName==null)
      return null;
    String[] split = pathName.split(pathSplit);
    if(split.length!=2)
      return null;
    return split[1];
  }


  public static String createPathName(String sourceName, String endName) {
    if (sourceName == null || endName == null)
      return null;
    return sourceName + pathSplit + endName;
  }
}
