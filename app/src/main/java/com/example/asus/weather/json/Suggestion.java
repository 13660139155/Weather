package com.example.asus.weather.json;

/**
 * 生活指数类
 * Created by ASUS on 2018/5/18.
 */

public class Suggestion {

    /* dressing, 穿衣指数 */
    public SugDressing sugDressing;
    /* uv,紫外线指数 */
    public SugUv sugUv;
    /* car_washing, 洗车指数 */
    public SugCarWashing sugCarWashing;
    /* travel, 旅游指数 */
    public SugTravel sugTravel;
    /* flu, 感冒指数 */
    public SugFishing sugFishing;
    /* sport, 运动指数 */
    public SugSport sugSport;

   public class SugDressing{
        public String dressingBrief;//简要建议
        public String dressingDetails;//详细建议
    }

    public class SugUv{
        public String uvBrief;
        public String uvDetails;
    }

    public class SugCarWashing{
        public String washingBrief;
        public String washingDetails;
    }

    public class SugTravel{
        public String travelBrief;
        public String travelDetails;
    }

    public class SugFishing{
        public String fishingBrief;
        public String fishingDetails;
    }

    public class SugSport{
        public String sportBrief;
        public String sportDetails;
    }
}

