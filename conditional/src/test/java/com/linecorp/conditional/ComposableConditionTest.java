/*
 * Copyright 2022 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.linecorp.conditional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.CompletionException;

import org.junit.jupiter.api.Test;

class ComposableConditionTest {

    @Test
    void matches() {
        final var condition = new ComposableCondition() {

            @Override
            protected Condition compose() {
                final var a = Condition.async(ctx -> true).alias("a");
                final var b = Condition.failed(new RuntimeException()).async().delay(1000).alias("b");
                return a.and(b);
            }
        }.alias("Composed");
        final var ctx = ConditionContext.of();
        assertThatThrownBy(() -> condition.matches(ctx))
                .isExactlyInstanceOf(CompletionException.class)
                .hasCauseInstanceOf(RuntimeException.class);
        assertThat(ctx.logs().size()).isEqualTo(4);
    }
}
