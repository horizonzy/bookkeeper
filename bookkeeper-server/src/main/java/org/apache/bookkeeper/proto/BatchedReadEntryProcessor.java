package org.apache.bookkeeper.proto;

import io.netty.buffer.ByteBuf;
import io.netty.util.Recycler;
import io.netty.util.ReferenceCounted;
import org.apache.bookkeeper.proto.BookieProtocol.BatchedReadRequest;
import org.apache.bookkeeper.util.ByteBufList;

import java.util.concurrent.ExecutorService;

class BatchedReadEntryProcessor extends ReadEntryProcessor {

    public static BatchedReadEntryProcessor create(BatchedReadRequest request,
                                            BookieRequestHandler requestHandler,
                                            BookieRequestProcessor requestProcessor,
                                            ExecutorService fenceThreadPool,
                                            boolean throttleReadResponses) {
        BatchedReadEntryProcessor rep = RECYCLER.get();
        rep.init(request, requestHandler, requestProcessor);
        rep.fenceThreadPool = fenceThreadPool;
        rep.throttleReadResponses = throttleReadResponses;
        requestProcessor.onReadRequestStart(requestHandler.ctx().channel());
        return rep;
    }

    @Override
    protected ReferenceCounted readData() throws Exception {
        // need to handle the max frame size
        ByteBufList data = null;
//        System.out.println(String.format("Batched read request %s, %d", request.toString(), ((BatchedReadRequest) request).getMaxCount()));
        for (int i = 0; i < ((BatchedReadRequest) request).getMaxCount(); i++) {
//            System.out.printf(String.format("Read individual entry %d", request.entryId + i));
            try {
                ByteBuf entry = requestProcessor.getBookie().readEntry(request.getLedgerId(), request.getEntryId() + i);
                if (data == null) {
                    data = ByteBufList.get(entry);
                } else {
                    data.add(entry);
                }
            } catch (Throwable e) {
                // todo, should return the existing data to the client
                // Release the readed data if there are failed read request.
                if (data != null) {
                    data.release();
                }
                throw e;
            }
        }
        return data;
    }

    @Override
    protected BookieProtocol.Response buildReadResponse(ReferenceCounted data) {
        return ResponseBuilder.buildBatchedReadResponse((ByteBufList) data, (BatchedReadRequest) request);
    }

    protected void recycle() {
        super.reset();
        this.recyclerHandle.recycle(this);
    }

    private final Recycler.Handle<BatchedReadEntryProcessor> recyclerHandle;

    private BatchedReadEntryProcessor(Recycler.Handle<BatchedReadEntryProcessor> recyclerHandle) {
        this.recyclerHandle = recyclerHandle;
    }

    private static final Recycler<BatchedReadEntryProcessor> RECYCLER = new Recycler<BatchedReadEntryProcessor>() {
        @Override
        protected BatchedReadEntryProcessor newObject(Recycler.Handle<BatchedReadEntryProcessor> handle) {
            return new BatchedReadEntryProcessor(handle);
        }
    };
}
