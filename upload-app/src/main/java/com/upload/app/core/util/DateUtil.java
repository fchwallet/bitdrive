package com.upload.app.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static Boolean comparetoTime(String beginTime,String endTime) throws ParseException {

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		 Date bt = sdf.parse(beginTime);
		 Date et = sdf.parse(endTime);
		 if (bt.before(et)){
			 return true;
		 }else{
			 if(beginTime.equals(endTime)){
				 return true;
			 }else{
				 return false;
			 }

		 }

    }

}
