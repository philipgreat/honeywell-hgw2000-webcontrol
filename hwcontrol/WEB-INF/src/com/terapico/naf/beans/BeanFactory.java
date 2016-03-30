package com.terapico.naf.beans;


public interface BeanFactory {
	public Object getBean(BeanLocation beanLocation);
	public String[] getBeanNames();
}
