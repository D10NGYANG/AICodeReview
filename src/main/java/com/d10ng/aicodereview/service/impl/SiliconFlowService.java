package com.d10ng.aicodereview.service.impl;

import com.d10ng.aicodereview.constant.Constants;
import com.d10ng.aicodereview.service.AIService;
import com.d10ng.aicodereview.util.OpenAIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * SiliconFlowService
 *
 * @author hmydk
 */
public class SiliconFlowService implements AIService {

    private static final Logger log = LoggerFactory.getLogger(SiliconFlowService.class);

    @Override
    public boolean generateByStream() {
        return true;
    }

    @Override
    public String generateCommitMessage(String content) throws Exception {
        return "null";
    }

    @Override
    public void generateCommitMessageStream(String content, Consumer<String> onNext)
            throws Exception {
        OpenAIUtil.getAIResponseStream(Constants.SiliconFlow, content, onNext);
    }

    @Override
    public boolean checkNecessaryModuleConfigIsRight() {
        return OpenAIUtil.checkNecessaryModuleConfigIsRight(Constants.SiliconFlow);
    }


}
