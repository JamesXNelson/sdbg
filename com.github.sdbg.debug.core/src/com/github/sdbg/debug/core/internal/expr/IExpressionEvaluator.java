/*
 * Copyright (c) 2012, the Dart project authors.
 * 
 * Licensed under the Eclipse Public License v1.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.sdbg.debug.core.internal.expr;

import org.eclipse.debug.core.model.IWatchExpressionListener;

/**
 * An interface to denote the ability to evaluate expressions.
 */
public interface IExpressionEvaluator {

  /**
   * Evaluate the given expression and return the result asynchronously using the provided
   * IWatchExpressionListener.
   * 
   * @param expression
   * @param listener
   */
  public void evaluateExpression(String expression, IWatchExpressionListener listener);

}
