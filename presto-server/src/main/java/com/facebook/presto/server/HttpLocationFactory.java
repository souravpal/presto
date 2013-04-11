/*
 * Copyright 2004-present Facebook. All Rights Reserved.
 */
package com.facebook.presto.server;

import com.facebook.presto.execution.QueryId;
import com.facebook.presto.execution.StageId;
import com.facebook.presto.execution.TaskId;
import com.facebook.presto.execution.LocationFactory;
import com.facebook.presto.metadata.Node;
import com.facebook.presto.metadata.NodeManager;
import com.google.common.base.Preconditions;
import io.airlift.http.server.HttpServerInfo;

import javax.inject.Inject;
import java.net.URI;

import static io.airlift.http.client.HttpUriBuilder.uriBuilderFrom;

public class HttpLocationFactory
        implements LocationFactory
{
    private final NodeManager nodeManager;
    private final URI baseUri;

    @Inject
    public HttpLocationFactory(NodeManager nodeManager, HttpServerInfo httpServerInfo)
    {
        this(nodeManager, httpServerInfo.getHttpUri());
    }

    public HttpLocationFactory(NodeManager nodeManager, URI baseUri)
    {
        this.nodeManager = nodeManager;
        this.baseUri = baseUri;
    }

    @Override

    public URI createQueryLocation(QueryId queryId)
    {
        Preconditions.checkNotNull(queryId, "queryId is null");
        return uriBuilderFrom(baseUri)
                .appendPath("/v1/query")
                .appendPath(queryId.toString())
                .build();
    }

    @Override
    public URI createStageLocation(StageId stageId)
    {
        Preconditions.checkNotNull(stageId, "stageId is null");
        return uriBuilderFrom(baseUri)
                .appendPath("v1/stage")
                .appendPath(stageId.toString())
                .build();
    }

    @Override
    public URI createLocalTaskLocation(TaskId taskId)
    {
        return createTaskLocation(nodeManager.getCurrentNode().get(), taskId);
    }

    @Override
    public URI createTaskLocation(Node node, TaskId taskId)
    {
        Preconditions.checkNotNull(node, "node is null");
        Preconditions.checkNotNull(taskId, "taskId is null");
        return uriBuilderFrom(node.getHttpUri())
                .appendPath("/v1/task")
                .appendPath(taskId.toString())
                .build();
    }
}
