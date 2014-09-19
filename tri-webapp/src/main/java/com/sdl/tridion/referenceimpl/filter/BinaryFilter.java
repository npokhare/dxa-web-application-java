package com.sdl.tridion.referenceimpl.filter;

import com.tridion.broker.StorageException;
import com.tridion.storage.*;
import com.tridion.storage.dao.BinaryContentDAO;
import com.tridion.storage.dao.BinaryVariantDAO;
import com.tridion.storage.dao.ItemDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.List;

public class BinaryFilter implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(BinaryFilter.class);

    // TODO: Publication id should be determined from configuration instead of being hard-coded
    private static final int PUBLICATION_ID = 48;

    private ServletContext servletContext;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        servletContext = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final String url = URLDecoder.decode(httpServletRequest.getRequestURI().replace(httpServletRequest.getContextPath(), ""), "UTF-8");
        if (!url.endsWith(".html")) {
            try {
                handleRequest(url);
            } catch (StorageException e) {
                throw new ServletException("Error while handling request: " + url, e);
            }
        }

        chain.doFilter(request, response);
    }

    private void handleRequest(String url) throws StorageException, IOException {
        LOG.debug("handleRequest: {}", url);

        final List<BinaryVariant> binaryVariants = ((BinaryVariantDAO) StorageManagerFactory.getDAO(PUBLICATION_ID,
                StorageTypeMapping.BINARY_VARIANT)).findByURL(url);
        if (binaryVariants == null || binaryVariants.isEmpty()) {
            LOG.debug("No binary variants found for: {}", url);
            return;
        }

        final BinaryVariant binaryVariant = binaryVariants.get(0);
        final BinaryMeta binaryMeta = binaryVariant.getBinaryMeta();
        final ItemMeta itemMeta = ((ItemDAO) StorageManagerFactory.getDAO(PUBLICATION_ID, StorageTypeMapping.ITEM_META))
                .findByPrimaryKey(binaryMeta.getPublicationId(), binaryMeta.getItemId());

        final File file = new File(servletContext.getRealPath(url));

        boolean refresh;
        if (file.exists()) {
            refresh = file.lastModified() < itemMeta.getLastPublishDate().getTime();
        } else {
            refresh = true;
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    throw new IOException("Failed to create parent directory for file: " + file);
                }
            }
        }

        if (refresh) {
            final BinaryContent binaryContent = ((BinaryContentDAO) StorageManagerFactory.getDAO(PUBLICATION_ID,
                    StorageTypeMapping.BINARY_CONTENT)).findByPrimaryKey(itemMeta.getPublicationId(),
                    itemMeta.getItemId(), binaryVariant.getBinaryVariantId().getVariantId());

            LOG.debug("Writing binary content to file: {}", file);
            Files.write(file.toPath(), binaryContent.getContent());
        } else {
            LOG.debug("File does not need to be refreshed: {}", file);
        }
    }

    @Override
    public void destroy() {
    }
}