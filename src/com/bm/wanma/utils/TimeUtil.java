package com.bm.wanma.utils;

import android.text.TextUtils;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {

    
    /**
     * @param time 时间戳
     * @return 该时间戳对应的日期字符
     * cm
     */
    public static String getTime(String time){
        if(time == null){
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date= null;
        try{
            date = new Date(Long.parseLong(time) * 1000);
        }catch (NumberFormatException e){

        }
        return format.format(date);
    }

    /**
     * @param time 时间戳
     * @return 该时间戳对应的日期字符
     */
    public static String getTimeForBespokeS(String time){
        if(time == null){
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd/HH:mm");
        Date date = new Date(Long.parseLong(time));
        return format.format(date);
    }
    /**
     * @param time 时间戳
     * @return 该时间戳对应的日期字符
     */
    public static String getTimeForBespokeL(long time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date(time);
        return format.format(date);
    }
    /**
     * @param time 时间戳
     * @return 该时间戳对应的日期字符
     */
    public static String getTimeDetail(String time){
        if(time == null){
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date = new Date(Long.parseLong(time) * 1000);
        return format.format(date);
    }

    /**
     * 把时间戳转化成对应的字符串
     *
     * @param time      时间戳
     * @param formatStr 对应的字符串"yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static String getTime(String time, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(((TextUtils.isEmpty(formatStr)) ? "yyyy-MM-dd HH:mm:ss" : formatStr));
        String str = "";
        if (!TextUtils.isEmpty(time)) {
            Date date = new Date(Long.parseLong(time) * 1000);
            str = format.format(date);
        } else {
            Date date = new Date(System.currentTimeMillis());
            str = format.format(date);
        }
        return str;
    }

    public static String getDefultTime(String time) {
        return getTime(time, "yyyy-M-d");
    }

    public static long get(int year, int monthOfYear, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.clear();
        c.set(year, monthOfYear - 1, dayOfMonth - 1);
        TextView t = null;
        t.setTag(1, "");
        return c.getTimeInMillis();
    }
    /**
     * 把分钟转化为小时分格式
     */
    
    public static String minConvertDayHourMin(int time){
    	String result = "0分钟";
    		String format;
    		Object[] array;
    		Integer days = (int)(time/(60*24));
    		Integer hours = (int)(time/60 -days*24);
    		Integer minutes = (int)(time-hours*60-days*24*60);
    		if(days > 0){
    			format = "%1$,d天%2$,d小时%3$,d分钟";
    			array = new Object[] {days,hours,minutes};
    		}else if(hours > 0){
    			format = "%1$,d小时%2$,d分钟";
    			array = new Object[] {hours,minutes};
    		}else {
    			format = "%1$,d分钟";
    			array = new Object[] {minutes};
    		}
    		result = String.format(format, array);
         return result;
    }

    /**
     * 获取时间差的字符串形式
     * 如：1天1时1分1秒
     * @param time 秒
     * @return
     */
    public static String getCutDown(long time) {

       // long time = endTime - System.currentTimeMillis() / 1000;

        int days = ((int) time) / (3600 * 24);
        int hours = ((int) time) % (3600 * 24) / 3600;
        int minutes = ((int) time) % (3600 * 24) % 3600 / 60;
        int seconds = ((int) time) % (3600 * 24) % 3600 % 60 % 60;

        String str = "";
        //1天之内  只显示小时  1小时之内的 只显示分钟  1分钟之内 的只显示秒
        if (days != 0) {
            str = days + "天";
        } else if (hours != 0) {
            str =  hours + "小时";
            //str = hours + "小时" + minutes + "分钟" + seconds + "秒";
        } else if (minutes != 0) {
            str =  minutes + "分钟";
        } else {
            str = seconds + "秒";
        }
        return str;
    }

    /**
     * 把时间戳 转换成 01:23：01 的时分秒的格式
     * @param time
     * @return
     */
    public static String getCutDown2(long time) {

        int days = ((int) time) / (3600 * 24);
        int hours = ((int) time) % (3600 * 24) / 3600;
        int minutes = ((int) time) % (3600 * 24) % 3600 / 60;
        int seconds = ((int) time) % (3600 * 24) % 3600 % 60 % 60;

        // 0 代表前面补充0
        // 4 代表长度为4
        // d 代表参数为正数型
        return String.format("%02d", hours) + ":" +  String.format("%02d", minutes) + ":" +  String.format("%02d", seconds) + "" ;
    }
    /**
     * 把时间戳 转换成 01:23的时分的格式
     * @param time
     * @return
     */
    public static String getCutDown3(long time) {

        int days = ((int) time) / (3600 * 24);
        int hours = ((int) time) % (3600 * 24) / 3600;
        int minutes = ((int) time) % (3600 * 24) % 3600 / 60;
        // 0 代表前面补充0
        // 4 代表长度为4
        // d 代表参数为正数型
        return String.format("%02d", hours) + "   小时   " +  String.format("%02d", minutes) +"  分钟" ;
    }
    /**
     * 把时间戳 转换成 N小时N分钟 的时分的格式
     * @param time
     * @return
     */
    public static String getCutDown4(long time) {

      /*  int hours = ((int) time) % (3600 * 24) / 3600;
        int minutes = ((int) time) % (3600 * 24) % 3600 / 60;
*/        
        String data = "";
        long days = time / (1000 * 60 * 60 * 24);  
        long hours = (time % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);  
        long minutes = (time % (1000 * 60 * 60)) / (1000 * 60);  
        long seconds = (time % (1000 * 60)) / 1000; 
       /* return days + " days " + hours + " hours " + minutes + " minutes "  
        + seconds + " seconds ";*/
        if(hours > 0){
        	data = String.format("%02d", hours) 
        			+"小时" +String.format("%02d", minutes) + "分钟" +  String.format("%02d", seconds) +"秒" ;
        }else if(minutes > 0){
        	data = String.format("%02d", minutes) + "分钟" +  String.format("%02d", seconds) +"秒" ;
        }else if(seconds > 0){
        	data = String.format("%02d", seconds) +"秒" ;
        }else {
        	data = "0秒" ;
        }
        
        // 0 代表前面补充0
        // 4 代表长度为4
        // d 代表参数为正数型
        return data;
    }
    public static String cntTimeDifference(String createdTime, String suffix) {
        if(createdTime.equals("")){
            return "从未刷新";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calNow = Calendar.getInstance();
        Calendar calCreated = Calendar.getInstance();
        try {
            calCreated.setTime(sdf.parse(createdTime));
            int year = calNow.get(Calendar.YEAR)
                    - calCreated.get(Calendar.YEAR);
            int month = calNow.get(Calendar.MONTH)
                    - calCreated.get(Calendar.MONTH);
            int day = calNow.get(Calendar.DAY_OF_MONTH)
                    - calCreated.get(Calendar.DAY_OF_MONTH);
            int hour = calNow.get(Calendar.HOUR_OF_DAY)
                    - calCreated.get(Calendar.HOUR_OF_DAY);
            int minute = calNow.get(Calendar.MINUTE)
                    - calCreated.get(Calendar.MINUTE);
            int total = minute + hour * 60 + day * 24 * 60 + month * 30 * 24
                    * 60 + year * 365 * 24 * 60;
            if (total > 365 * 24 * 60) {
                return total / (365 * 24 * 60) + "年" + suffix;
            }
            if (total > 30 * 24 * 60) {
                return total / (30 * 24 * 60) + "月" + suffix;
            }
            if (total > 24 * 60) {
                return total / (24 * 60) + "天" + suffix;
            }
            if (total > 60) {
                return total / 60 + "小时" + suffix;
            }
            if (total > 0) {
                return total + "分钟" + suffix;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "未知时间";
        }
        return "刚刚";
    }


    /**
     * 把时间字符串，转换成时间戳（ms）
     * eg：“2014/7/28 0:0:0” --> 1406476800
     * @param timeStr 2014/7/28 0:0:0
     * @param formatStr “yyyy/M/d/ H:m:s”
     * @return
     */
    public static long getTimestamp(String timeStr, String formatStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
        long l = 0L;
        try {
            Date date = sdf.parse(timeStr);
            l = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return l;
    }

    /**
     * 把形如20140918155055的日期格式转换成 yyyy-MM-dd HH:mm
     * @param timeStr
     * @param formatStr 可以为空
     * @return
     */
    public static String getTimeFromStr (String timeStr, String formatStr) {
        String format = TextUtils.isEmpty(formatStr) ? "yyyyMMddHHmmsss": formatStr;//2014 09 19 11 18 110
        long timeSTamp = getTimestamp(timeStr, format);
        return getTimeDetail(timeSTamp+"");
    }

    /**
     * 获取当前日期是星期几<br>
     *
     * @param dt
     * @return 当前日期是星期几
     */
    public static String getWeekOfDate(Date dt) {
        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public static String getTimeDay(Date date) {
        boolean sameYear = false;
        String todySDF = "HH:mm";
        String yesterDaySDF = "昨天";
        String beforYesterDaySDF = "前天";
        String otherSDF = "MM-dd";
        String otherYearSDF = "yyyy-MM-dd";
        SimpleDateFormat sfd = null;
        String time = "";
        Calendar dateCalendar = Calendar.getInstance();
        dateCalendar.setTime(date);
        Date now = new Date();
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTime(now);
        todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
        todayCalendar.set(Calendar.MINUTE, 0);

        if (dateCalendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR)) {
            sameYear = true;
        } else {
            sameYear = false;
        }

        if (dateCalendar.after(todayCalendar)) {// 判断是不是今天
            sfd = new SimpleDateFormat(todySDF);
            time = "今天";
            return time;
        } else {
            todayCalendar.add(Calendar.DATE, -1);
            if (dateCalendar.after(todayCalendar)) {// 判断是不是昨天
                // sfd = new SimpleDateFormat(yesterDaySDF);
                // time = sfd.format(date);
                time = yesterDaySDF;
                return time;
            }
            todayCalendar.add(Calendar.DATE, -2);
            if (dateCalendar.after(todayCalendar)) {// 判断是不是前天
                // sfd = new SimpleDateFormat(beforYesterDaySDF);
                // time = sfd.format(date);
                time = beforYesterDaySDF;
                return time;
            }else {
                return "更早";
            }
        }

//        if (sameYear) {
//            sfd = new SimpleDateFormat(otherSDF);
//            time = sfd.format(date);
//        } else {
//            sfd = new SimpleDateFormat(otherYearSDF);
//            time = sfd.format(date);
//        }

//        return time;
    }
    /**
	 * 计算两个日期型的时间相差多少时间
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @return
	 */
	public static String twoDateDistance(Date startDate, Date endDate) {

		if (startDate == null || endDate == null) {
			return null;
		}
		long timeLong = endDate.getTime() - startDate.getTime();
		if (Math.abs(timeLong) < 60 * 1000)
			return timeLong / 1000 + "";

		else if (Math.abs(timeLong) < 60 * 60 * 1000) {
			timeLong = timeLong / 1000 / 60;
			return timeLong + "";

		} else if (Math.abs(timeLong) < 60 * 60 * 24 * 1000) {
			timeLong = timeLong / 60 / 60 / 1000;
			return timeLong + "";

		} else if (Math.abs(timeLong) < 60 * 60 * 24 * 1000 * 7) {
			timeLong = timeLong / 1000 / 60 / 60 / 24;
			return timeLong + "";
			// return timeLong + "天前";
		}
		if (Math.abs(timeLong) > 60 * 60 * 24 * 1000 * 7) {
			timeLong = timeLong / 1000 / 60 / 60 / 24;
			return timeLong + "";
			// return timeLong + "天前";
		}
		// else if (Math.abs(timeLong)<60*60*24*1000*7*4){
		// timeLong = timeLong/1000/ 60 / 60 / 24/7;
		// return timeLong + "周前";
		// }
		else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));
			return sdf.format(startDate);
		}
	}
	/**
	 * 计算金额
	 * 
	 * @param argStr
	 * @return
	 */
	public static String getFloatDotStr(String argStr) {
		float arg = Float.valueOf(argStr);
		DecimalFormat fnum = new DecimalFormat("##0.00");
		return fnum.format(arg);
	}
	
	
	public static boolean isNull(String str) {
		if ("".equals(str) && str == null) {
			return true;
		} else {
			return false;
		}

	}
	
	/**
	 * 将时间unix转换为int类型
	 * 
	 * @param timeString
	 * @param format
	 * @return
	 */
	public static String getDateToString(String timeString) {
		String time = timeString.replace(":", "-");
		return time;
	}

	public static String getDateToStringShu(String timeString) {
		String time = timeString.replace(":", "/");
		return time;
	}
	
}
