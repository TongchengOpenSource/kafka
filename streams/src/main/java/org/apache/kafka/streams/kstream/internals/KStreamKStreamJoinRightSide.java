/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.kstream.internals;

import org.apache.kafka.streams.kstream.ValueJoinerWithKey;
import org.apache.kafka.streams.kstream.internals.KStreamImplJoin.TimeTrackerSupplier;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.state.internals.LeftOrRightValue;
import org.apache.kafka.streams.state.internals.TimestampedKeyAndJoinSide;

import java.util.Optional;

class KStreamKStreamJoinRightSide<K, VLeft, VRight, VOut> extends KStreamKStreamJoin<K, VLeft, VRight, VOut, VRight, VLeft> {

    KStreamKStreamJoinRightSide(final String otherWindowName,
                                final JoinWindowsInternal windows,
                                final ValueJoinerWithKey<? super K, ? super VRight, ? super VLeft, ? extends VOut> joiner,
                                final boolean outer,
                                final Optional<String> outerJoinWindowName,
                                final TimeTrackerSupplier sharedTimeTrackerSupplier) {
        super(otherWindowName, windows, joiner, outer, outerJoinWindowName, windows.afterMs, windows.beforeMs,
                sharedTimeTrackerSupplier);
    }

    @Override
    public Processor<K, VRight, K, VOut> get() {
        return new KStreamKStreamRightJoinProcessor();
    }

    private class KStreamKStreamRightJoinProcessor extends KStreamKStreamJoinProcessor {
        @Override
        public TimestampedKeyAndJoinSide<K> makeThisKey(final K key, final long timestamp) {
            return TimestampedKeyAndJoinSide.makeRight(key, timestamp);
        }

        @Override
        public LeftOrRightValue<VLeft, VRight> makeThisValue(final VRight thisValue) {
            return LeftOrRightValue.makeRightValue(thisValue);
        }

        @Override
        public TimestampedKeyAndJoinSide<K> makeOtherKey(final K key, final long timestamp) {
            return TimestampedKeyAndJoinSide.makeLeft(key, timestamp);
        }

        @Override
        public VRight getThisValue(final LeftOrRightValue<? extends VLeft, ? extends VRight> leftOrRightValue) {
            return leftOrRightValue.rightValue();
        }

        @Override
        public VLeft getOtherValue(final LeftOrRightValue<? extends VLeft, ? extends VRight> leftOrRightValue) {
            return leftOrRightValue.leftValue();
        }
    }
}
