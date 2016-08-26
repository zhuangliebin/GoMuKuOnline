package test;

import java.util.Random;

public class Test {
	public static void main(String[] args) {
		Random random = new Random();
			for(int i=0;i<100;i++)
			{
				int whoFirst=random.nextInt(2);//随机生成指定先手用户
				System.out.println(whoFirst);
			}
	}
}
