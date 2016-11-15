package com.xh.image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import org.im4java.core.IdentifyCmd;
import org.im4java.process.ArrayListOutputConsumer;
import org.im4java.process.StandardStream;

public class GMIm4java {

	/**
	 * @param args
	 * author：xionghui
	 * data：2016-11-7 下午5:15:13
	 */
	public static void main(String[] args) {
		
		String picPath = "C:/Users/Administrator/Desktop/ff.jpg";  //需要处理图片地址
		String drawPicPath = "C:/Users/Administrator/Desktop/111.jpg";  //输出图片地址
		Map<String, Object> info = new HashMap<String, Object>();
		try {
			/*获取图片信息*/
			/*info = getImageInfo(picPath);     
			Integer width = Integer.parseInt(info.get("width").toString());     //图片宽
			Integer height = Integer.parseInt(info.get("height").toString());   //图片高
			String directory = info.get("directory").toString();                //图片路劲
			String filename = info.get("filename").toString();                  //图片文件名
			String filelength = info.get("filelength").toString();              //图片文件长度
			System.out.println("图片名："+filename+"--图片路径："+directory+"--图片宽*高："+width+"*"+height+"--图片长度："+filelength);*/
			/*压缩图片*/
			//compressImg(picPath, drawPicPath , 350, 350);
			/*补白图片*/
			//fillImage(picPath, drawPicPath);
			/*裁剪图片*/
//			cutImage(picPath, drawPicPath, 350, 500, 0, 0);
			rotateImage(picPath, drawPicPath, -90d);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IM4JavaException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 压缩图片
	 * @param picPath        待处理图片地址
	 * @param drawPicPath    处理后图片地址
	 * @param width          压缩宽度
	 * @param height         压缩高度
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws IM4JavaException
	 * author：xionghui
	 * data：2016-11-8 下午5:51:12
	 */
	public static void compressImg(String picPath, String drawPicPath, int width, int height) throws IOException, InterruptedException, IM4JavaException{
		IMOperation op = new IMOperation();
		op.addImage(picPath);
		op.resize(width, height);  //压缩
		op.quality(80d);           //图片品质
		op.addImage(drawPicPath);	
		ConvertCmd cmd = new ConvertCmd(true);
		String osName = System.getProperty("os.name").toLowerCase();
		if(osName.indexOf("win") >= 0){
			cmd.setSearchPath("D:/GraphicsMagick-1.3.25-Q8");
		}
		cmd.setErrorConsumer(StandardStream.STDERR);
		cmd.run(op);
	}
	
	/**
	 * 获取图片信息 
	 * @param imagePath      图片地址
	 * @return
	 * author：xionghui
	 * data：2016-11-9 下午12:04:37
	 * @throws IM4JavaException 
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public static Map<String, Object> getImageInfo(String picPath) throws IOException, InterruptedException, IM4JavaException{
		Map<String, Object> infoMap = new HashMap<String, Object>();
		IMOperation op = new IMOperation();
		op.format("%w,%h,%d,%f,%b%[EXIF:DateTimeOriginal]");
		op.addImage(picPath);
		IdentifyCmd iCmd = new IdentifyCmd(true);
		ArrayListOutputConsumer output = new ArrayListOutputConsumer();
		iCmd.setOutputConsumer(output);
		iCmd.run(op);
		ArrayList<String> cmdOutput = output.getOutput();
		if (cmdOutput.size() != 1) return null;
		String line = cmdOutput.get(0);
		String[] lines = line.split(",");
		infoMap.put("width", lines[0]);      //图片宽
		infoMap.put("height", lines[1]);     //图片高
		infoMap.put("directory", lines[2]);  //图片文件路径
		infoMap.put("filename", lines[3]);   //图片文件名
		infoMap.put("filelength", lines[4]); //图片文件长度
		return infoMap;
	}
	/**
	 * 补白
	 * @param picPath       待处理图片地址
	 * @param drawPicPath   处理后图片地址
	 * author：xionghui
	 * data：2016-11-9 下午2:49:42
	 * @throws IM4JavaException 
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public static void fillImage(String picPath, String drawPicPath) throws IOException, InterruptedException, IM4JavaException{
		Map<String, Object> info = GMIm4java.getImageInfo(picPath);
		Integer width = Integer.parseInt(info.get("width").toString());
		Integer height = Integer.parseInt(info.get("height").toString());
		Integer fillSize = null;
		/*拿宽高比较，补短的那一边后变成正方形，也可以根据指定大小补白*/
		if(width > height){
			fillSize = width;
		}else{
			fillSize = height;
		}
		IMOperation op = new IMOperation();
		op.addImage(picPath);
		op.background("white");
		op.gravity("center");
		op.extent(fillSize);    //正方形，长宽只需定义一个就行
		op.addImage(drawPicPath);
		ConvertCmd cmd = new ConvertCmd(true);
		cmd.run(op);
	}
	
	/**
	 * 
	 * @param picPath        待处理图片地址
	 * @param drawPicPath    处理后图片地址
	 * @param x              起始X坐标
	 * @param y              起始Y坐标
	 * @param width          裁剪宽度
	 * @param height         裁剪高度
	 * author：xionghui
	 * data：2016-11-9 下午3:59:54
	 * @throws IM4JavaException 
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public static void cutImage(String picPath, String drawPicPath, int width, int height, int x, int y) throws IOException, InterruptedException, IM4JavaException{
		IMOperation op = new IMOperation();
		op.addImage(picPath);
		/** width：裁剪的宽度 * height：裁剪的高度 * x：裁剪的横坐标 * y：裁剪纵坐标 */
		op.crop(width, height, x, y);
		op.addImage(drawPicPath);
		ConvertCmd cmd = new ConvertCmd(true);
		cmd.run(op);
	}
	
	/**
	 * 
	 * @param picPath       待处理图片地址
	 * @param drawPicPath   处理后图片地址
	 * @param degree        旋转角度
	 * author：xionghui
	 * data：2016-11-9 下午4:19:17
	 * @throws IM4JavaException 
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public static void rotateImage(String picPath, String drawPicPath, Double degree) throws IOException, InterruptedException, IM4JavaException{
		/*格式化角度*/
		if(degree <= 0){
			degree += 360;
		}
		degree %= 360;
		IMOperation op = new IMOperation();
		op.addImage(picPath);
		op.rotate(degree);
		op.addImage(drawPicPath);
		ConvertCmd cmd= new ConvertCmd(true);
		cmd.run(op);
	}
}














