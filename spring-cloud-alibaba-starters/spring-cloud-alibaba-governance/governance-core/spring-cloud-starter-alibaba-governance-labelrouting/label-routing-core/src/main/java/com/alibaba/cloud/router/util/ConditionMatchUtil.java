/*
 * Copyright 2013-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.cloud.router.util;

import java.util.regex.Pattern;

/**
 * @author HH
 */
public class ConditionMatchUtil {

	/**
	 * Sign of exact.
	 */
	public static final String EXACT = "exact";

	/**
	 * Sign of regex.
	 */
	public static final String REGEX = "regex";

	/**
	 * Sign of contain.
	 */
	public static final String PREFIX = "prefix";

	/**
	 * Sign of contain.
	 */
	public static final String CONTAIN = "contain";

	/**
	 * Sign of greater.
	 */
	public static final String GREATER = ">";

	/**
	 * Sign of less.
	 */
	public static final String LESS = "<";

	/**
	 * Sign of equal.
	 */
	public static final String EQUAL = "=";

	/**
	 * Sign of no equal.
	 */
	public static final String NOT_EQUAL = "not_equal";

	public boolean exactMatch(String one, String another) {
		return one.equals(another);
	}

	public boolean regexMatch(String regex, String path) {
		return Pattern.matches(regex, path);
	}

	public boolean containMatch(String sub, String base) {
		return base.contains(sub);
	}

	public boolean prefixMatch(String prefix, String str) {
		return str.startsWith(prefix);
	}

	public boolean greaterMatch(String str, String comparor) {
		return Integer.parseInt(comparor) > Integer.parseInt(str);
	}

	public boolean lessMatch(String str, String comparor) {
		return Integer.parseInt(comparor) < Integer.parseInt(str);
	}

	public boolean noEqualMatch(String str, String comparor) {
		return Integer.parseInt(str) == Integer.parseInt(comparor);
	}

}