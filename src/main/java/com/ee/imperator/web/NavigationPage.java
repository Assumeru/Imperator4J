package com.ee.imperator.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Comparator;

import org.ee.web.request.filter.RequestFilter;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface NavigationPage {
	String name();

	int index();

	public static class NavigationSorter implements Comparator<RequestFilter> {
		@Override
		public int compare(RequestFilter o1, RequestFilter o2) {
			NavigationPage p1 = o1.getClass().getAnnotation(NavigationPage.class);
			NavigationPage p2 = o2.getClass().getAnnotation(NavigationPage.class);
			if(p1 == null || p2 == null) {
				throw new IllegalArgumentException("Both pages must be annotated with NavigationPage");
			}
			return Integer.compare(p1.index(), p2.index());
		}
	}
}
