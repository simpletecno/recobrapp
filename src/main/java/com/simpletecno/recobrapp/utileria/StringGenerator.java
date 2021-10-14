/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.simpletecno.recobrapp.utileria;

/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

import com.vaadin.shared.util.SharedUtil;

public class StringGenerator {
	static String[] strings = { "lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "quid", "securi", "etiam",
			"tamquam", "eu", "fugiat", "nulla", "pariatur" };
	int stringCount = -1;

	String nextString(boolean capitalize) {
		if (++this.stringCount >= strings.length) {
			this.stringCount = 0;
		}
		return capitalize ? SharedUtil.capitalize(strings[this.stringCount]) : strings[this.stringCount];
	}

}