package com.leon.datalink.node.actor;

import akka.actor.Cancellable;
import com.leon.datalink.core.exception.impl.DataValidateException;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.node.AbstractNodeActor;
import com.leon.datalink.node.context.NodeContext;
import com.leon.datalink.node.context.Output;
import javafx.util.Pair;
import scala.concurrent.duration.Duration;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;


public class SpeedNodeActor extends AbstractNodeActor {

    private final Queue<Pair<Object, Output>> queue = new LinkedList<>();

    private Integer limit;

    private Cancellable cancellable;

    @Override
    public void create(NodeContext context, Output output) throws Exception {
        String interval = properties.getString("interval");
        String intervalUnit = properties.getString("intervalUnit");

        if (StringUtils.isEmpty(interval) || StringUtils.isEmpty(intervalUnit)) throw new DataValidateException();

        limit = properties.getInteger("limit");

        cancellable = getContext().system().scheduler().scheduleAtFixedRate(
                Duration.Zero(),
                Duration.create(Long.parseLong(interval), TimeUnit.valueOf(intervalUnit)),
                this::send,
                getContext().dispatcher()
        );
    }

    @Override
    public void destroy() throws Exception {
        if (cancellable != null) {
            cancellable.cancel();
        }
    }

    @Override
    public void onInput(NodeContext context, Object data, Output output) {
        queue.add(new Pair<>(data, output));
    }

    private void send() {
        for (int i = 0; i < limit; i++) {
            if (queue.isEmpty()) return;
            Pair<Object, Output> poll = queue.poll();
            poll.getValue().out(poll.getKey());
        }
    }


}
