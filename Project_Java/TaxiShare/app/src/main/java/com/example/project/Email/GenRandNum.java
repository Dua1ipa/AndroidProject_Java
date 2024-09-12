package com.example.project.Email;

import java.util.Random;

public class GenRandNum {
    private static  final  String TAG = "GenRandNum";
    private  static  String digits = "0123456789";
    final static int codeLen = 4;

    // 랜덤 인증번호 생성 함수 //
    public static String genRandNum(){
        Random random = new Random();

        StringBuilder result = new StringBuilder(codeLen);  //인증번호 길이 설정
        for(int i = 0; i < 7; i++){
            result.append(digits.charAt(random.nextInt(digits.length())));
        }
        return result.toString();
    }
}
