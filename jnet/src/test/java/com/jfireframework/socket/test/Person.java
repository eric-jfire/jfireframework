package com.jfireframework.socket.test;

public class Person
{
	private String	name;
	private int		age;
	private float	weight;
	
	public Person(String name, int age, float weight)
	{
		this.age = age;
		this.name = name;
		this.weight = weight;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public int getAge()
	{
		return age;
	}
	
	public void setAge(int age)
	{
		this.age = age;
	}
	
	public float getWeight()
	{
		return weight;
	}
	
	public void setWeight(float weight)
	{
		this.weight = weight;
	}
	
	public String toString()
	{
		return "name:" + name + ",age:" + age + ",weight:" + weight;
	}
}
