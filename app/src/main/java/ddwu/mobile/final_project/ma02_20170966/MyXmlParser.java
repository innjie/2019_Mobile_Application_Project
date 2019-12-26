package ddwu.mobile.final_project.ma02_20170966;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;


public class MyXmlParser {

//    xml에서 읽어들일 태그를 구분한 enum  → 정수값 등으로 구분하지 않고 가독성 높은 방식을 사용
    private enum TagType { NONE, CITY, WEATHER};     // 해당없음, rank, movieNm, openDt, movieCd
//    parsing 대상인 tag를 상수로 선언
    private final static String FAULT_RESULT = "faultResult";
    private final static String CURRENT_TAG = "current";
    private final static String CITY_TAG = "city";
    private final static String WEATHER_TAG = "weather";

    private XmlPullParser parser;


    public MyXmlParser() {
//        xml 파서 관련 변수들은 필요에 따라 멤버변수로 선언 후 생성자에서 초기화
//        파서 준비

        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }
    public ArrayList<Weather> parse(String xml) {
        ArrayList<Weather> resultList = new ArrayList(); //dto 를 저장하기 위한 arrayList
        Weather dto = null;

        TagType tagType = TagType.NONE;     //  태그를 구분하기 위한 enum 변수 초기화

        try {
            parser.setInput(new StringReader(xml));
            int eventType = parser.getEventType();      // 태그 유형 구분 변수 준비

            while (eventType != XmlPullParser.END_DOCUMENT) {  // parsing 수행 - for 문 또는 while 문으로 구성
                 switch (eventType) {
                     case XmlPullParser.START_DOCUMENT:
                         break;
                         case XmlPullParser.START_TAG:
                             String tag = parser.getName();
                             if (tag.equals(CURRENT_TAG)) {    // 새로운 항목을 표현하는 태그를 만났을 경우 dto 객체 생성
                                 dto = new Weather();
                                 Log.d("aaa", "DTO MAKE");
                             } else if (tag.equals(CITY_TAG)) {
                                 tagType = TagType.CITY;
                                 Log.d("aaa", parser.getAttributeValue(null, "name"));
                                 dto.setCity(parser.getAttributeValue(null, "name"));
                                 Log.d("aaa", "CITY TAG");
                             } else if (tag.equals(WEATHER_TAG)) {
                                 tagType = TagType.WEATHER;

                                 dto.setWeather(parser.getAttributeValue(null, "value"));
                                 Log.d("aaa",parser.getAttributeValue(null, "value"));
                             } else if (tag.equals(FAULT_RESULT)) {
                                 return null;
                             }
                             break;

                             case XmlPullParser.END_TAG:
                                 if (parser.getName().equals(CURRENT_TAG)) {
                                     resultList.add(dto);
                                 }
                                 break;
                     case XmlPullParser.TEXT:
                         switch(tagType) {       // 태그의 유형에 따라 dto 에 값 저장
                              case CITY:

                                  break;
                             case WEATHER:
                                 dto.setWeather(parser.getText());
                                 break;
                         }
                         tagType = TagType.NONE;
                         break;
                 }
                eventType = parser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultList;
    }
}
