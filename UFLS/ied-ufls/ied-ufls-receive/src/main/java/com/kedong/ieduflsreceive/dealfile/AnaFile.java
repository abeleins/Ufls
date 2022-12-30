package com.kedong.ieduflsreceive.dealfile;


import java.io.*;
import java.util.Arrays;
import java.util.Vector;


/**
 * 解析文件
 */
public class AnaFile {
    private BufferedReader br = null;

    private boolean isLocal;
    private String pathName;
    private String charsetName = "gbk";

    public AnaFile() {

    }

    /**
     * 本地文件读取
     */
    public AnaFile(String pathName) throws Exception {
        this(pathName, true);
    }

    /**
     * 解析文件
     *
     * @param pathName 文件路径
     * @param isLocal  文件是否在本地
     */
    public AnaFile(String pathName, boolean isLocal) throws Exception {
        this.isLocal = isLocal;
        this.pathName = pathName;
        init();
    }

    private void init() throws Exception {
        if (isLocal) {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(pathName), charsetName));
        } else {
            br = new BufferedReader(new StringReader(new String("")));
        }
    }


    /**
     * 解析数据<br>
     * fileModel开始符不能为空<br>
     * fileModel结束符不能为空<br>
     * fileModel分割符不能为空<br>
     *
     * @throws IllegalArgumentException
     */
    public void analysisData(FileModel fileModel) throws IllegalArgumentException {
        // 分割符不能为空
        if (fileModel.getMark() == null
                || fileModel.getMark().trim().isEmpty()) {
            throw new IllegalArgumentException("分隔符不能为空！");
        }
        // 开始符不能为空
        if (fileModel.getStartMark() == null
                || fileModel.getStartMark().trim().isEmpty()) {
            throw new IllegalArgumentException("开始符不能为空！");
        }
        // 开始符不能为空
        if (fileModel.getEndMark() == null
                || fileModel.getEndMark().trim().isEmpty()) {
            throw new IllegalArgumentException("结束符不能为空！");
        }
        try {
            br.mark(314572800);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        // 无用数据
        StringBuffer rubbishData = new StringBuffer();
        rubbishData.append("解析" + fileModel.getStartMark() + "CIME过滤掉的数据:");
        rubbishData.append("\n");
        String lineContent;
        try {
            //TODO 注释里存放的日期 删除下一行
            String commentDate = null;
            //读取第一行，注释
            while ((lineContent = br.readLine()) != null) {
                if (isNote(lineContent)) {
                    String comment = lineContent.substring(2, lineContent.length() - 2);
                    String[] split = comment.split("'");
                    commentDate = split[1];
                    break;
                }
            }
            // 开始符匹配
            while ((lineContent = br.readLine()) != null
                    && !fileModel.getStartMark().equalsIgnoreCase(lineContent)) {
                rubbishData.append(lineContent);
                rubbishData.append("\n");
            }
            if (lineContent == null) {
                System.out.println(rubbishData.toString());
                throw new IllegalArgumentException("数据不符合标准！");
            }
            // 设置头信息（开始符）
            fileModel.setHead(lineContent.trim());


            // 域信息解析,域名以@开始
            lineContent = br.readLine();
            while (lineContent != null && !(isAttribute(lineContent)
                    || isColName(lineContent) || lineContent.contains(fileModel.getEndMark()) || lineContent
                    .trim().startsWith("#"))) {
                rubbishData.append(lineContent);
                rubbishData.append("\n");
                lineContent = br.readLine();
            }
            if (lineContent == null) {
                System.out.println(rubbishData.toString());
                throw new IllegalArgumentException("数据不符合标准！");
            }
            if (isAttribute(lineContent)) {
                lineContent = lineContent.trim();
                // 设置域名
                String[] attNames = lineContent.split(fileModel.getMark());
                lineContent = "";
                try {
                    Vector<String> atts = new Vector<>(Arrays.asList(attNames));
                    if (atts.size() > 0 && atts.get(0).equals("@"))
                        atts.remove(0);
                    if (atts.size() > 0) {
                        atts.remove(0);
                    }
                    //TODO 删除下一行
                    atts.add("commentDate");
                    fileModel.setAttNames(atts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 列名解析
            while (lineContent != null && !(isColName(lineContent)
                    || lineContent.contains(fileModel.getEndMark()) || lineContent
                    .trim().startsWith("#"))) {
                if (!lineContent.isEmpty()) {
                    rubbishData.append(lineContent);
                    rubbishData.append("\n");
                }
                lineContent = br.readLine();
            }
            if (lineContent == null) {
                System.out.println(rubbishData.toString());
                throw new IllegalArgumentException("数据不符合标准！");
            }
            if (isColName(lineContent)) {
                lineContent = lineContent.trim();
                String[] colNames = lineContent.split(fileModel.getMark());
                try {
                    Vector<String> cols = new Vector<String>(Arrays.asList(colNames));
                    if (cols.size() > 0 && cols.get(0).toString().equals("//"))
                        cols.remove(0);
                    if (cols.size() > 0) {
                        cols.remove(0);
                    }
                    fileModel.setColumnName(cols);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                lineContent = "";
            }

            Vector<Vector<String>> data = new Vector<>();
            // 结束符是否为空
            boolean endMark = false;
            if (fileModel.getEndMark() != null
                    && !fileModel.getEndMark().equals("")) {
                endMark = true;
            }

            do {
                lineContent = lineContent.trim();
                // 结束符判断
                if (endMark) {
                    if (lineContent.contains(fileModel.getEndMark())) {
                        fileModel.setTail(lineContent);
                        break;
                    }
                }
                if (lineContent.equals("") || isNote(lineContent)
                        || !lineContent.startsWith("#")) {
                    rubbishData.append(lineContent);
                    rubbishData.append("\n");
                    continue;
                }

                Vector<String> rowData = new Vector<>();
                // 解析数据中存在特殊数据，以单引号括起来的情况
                if (lineContent.contains("'")) {
                    String[] v = lineContent.split("'");
                    int len = v.length;
                    for (int i = 0; i < len; i++) {
                        String s = v[i].trim();
                        if (i % 2 == 0 || (len % 2 == 1 && i == len - 1)) {
                            String[] attValue = s.split(fileModel.getMark());
                            for (String value : attValue) {
                                if (!value.trim().equals("")) {
                                    String str = value.trim();
                                    if (!"-".equals(str))
                                        rowData.add(str);
                                    else rowData.add(null);
                                }
                            }
                        } else {
                            if (!"-".equals(s))
                                rowData.add(s);
                            else rowData.add(null);
                        }
                    }
                } else {
                    String[] attValue = lineContent.split(fileModel.getMark());
                    for (String value : attValue) {
                        String s = value.trim();
                        if (!"-".equals(s))
                            rowData.add(s);
                        else rowData.add(null);
                    }
                }

                // 要是存在#要去掉
                if (rowData.size() > 0 && rowData.get(0).toString().equals("#")) {
                    rowData.remove(0);
                }
                if (rowData.size() > 0) {
                    // 去掉CIME中的序号
                    rowData.remove(0);
                }
                //TODO 删除下一行
                rowData.add(commentDate);
                data.add(rowData);
            } while ((lineContent = br.readLine()) != null);

//			// 数据
//			while ((lineContent = br.readLine()) != null) {
//				lineContent = lineContent.trim();
//				// 结束符判断
//				if(endMark){
//					if(lineContent.contains(fileModel.getEndMark())){
//						fileModel.setTail(lineContent);
//						break;
//					};
//				}
//				if (lineContent.equals("") || isNote(lineContent)
//						|| !lineContent.startsWith("#")) {
//					rubbishData.append(lineContent);
//					rubbishData.append("\n");
//					continue;
//				}
//
//
//				Vector<String> rowData = new Vector<String>();
//				// 解析数据中存在特殊数据，以单引号括起来的情况
//				if(lineContent.contains("'")){
//					String[] v = lineContent.split("'");
//					int len = v.length;
//					for (int i = 0; i < len; i++) {
//						if (i % 2 == 0 || (len % 2 == 1 && i == len - 1)) {
//							String[] attValue = v[i].trim().split(
//									fileModel.getMark());
//							for (String value : attValue) {
//								if(!value.trim().equals("")){
//									rowData.add(new String(value.trim()));
//								}
//							}
//						} else {
//							rowData.add(new String(v[i].trim()));
//						}
//					}
//				}else{
//					String[] attValue = lineContent.split(fileModel.getMark());
//					for (String value : attValue) {
//						rowData.add(new String(value.trim()));
//					}
//				}
//
//				// 要是存在#要去掉
//				if (rowData.size()>0 && rowData.get(0).toString().equals("#")) {
//					rowData.remove(0);
//				}
//				if(rowData.size()>0){
//					// 去掉CIME中的序号
//					rowData.remove(0);
//				}
//				data.add(rowData);
//			}
            fileModel.setData(data);

            System.out.println(rubbishData.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            br.reset();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 解析多组数据<br>
     * 分割符不能为空
     *
     * @throws IllegalArgumentException
     */
    public void analysisData(Vector<FileModel> fileModels) {
        for (FileModel fileModel : fileModels) {
            try {
                analysisData(fileModel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断是否是注释<br>
     * <! ********* !>
     */
    public boolean isNote(String text) {

//		if(text.matches("[/]{2}.*$")||text.matches("[<][!].*[!][>]$")){
//			return true;
//		}
        if (text.trim().matches("[<][!].*[!][>]$")) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是列名<br>
     * 以双斜杠开头
     */
    public boolean isColName(String text) {
        if (text.trim().matches("[/]{2}.*$")) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否是域<br>
     * 以@开头
     */
    public boolean isAttribute(String text) {
        if (text.trim().matches("[@].*$")) {
            return true;
        }
        return false;
    }


    /**
     * 关闭文件
     */
    public void close() {
        if (br != null) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            br = null;
        }
    }
}
