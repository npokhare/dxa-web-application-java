package com.sdl.webapp.dd4t;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.Page;
import org.dd4t.contentmodel.GenericPage;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.SerializationException;
import org.dd4t.core.factories.PageFactory;
import org.dd4t.core.filters.FilterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

/**
 * Implementation of {@code ContentProvider} that uses DD4T to provide content.
 */
@Component
public final class DD4TContentProvider implements ContentProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DD4TContentProvider.class);

    private static final String DEFAULT_PAGE_NAME = "index.html";
    private static final String DEFAULT_PAGE_EXTENSION = ".html";

    private static interface TryFindPage<T> {
        public T tryFindPage(String path, int publicationId) throws ContentProviderException;
    }

    private final org.dd4t.core.factories.PageFactory dd4tPageFactory;

    private final PageBuilder pageBuilder;

    @Autowired
    public DD4TContentProvider(PageFactory dd4tPageFactory, PageBuilder pageBuilder) {
        this.dd4tPageFactory = dd4tPageFactory;
        this.pageBuilder = pageBuilder;
    }

    @Override
    public Page getPageModel(String path, final Localization localization) throws ContentProviderException {
        return findPage(path, localization, new TryFindPage<Page>() {
            @Override
            public Page tryFindPage(String path, int publicationId) throws ContentProviderException {
                final GenericPage genericPage;
                try {
                    genericPage = (GenericPage) dd4tPageFactory.findPageByUrl(path, publicationId);
                } catch (ItemNotFoundException e) {
                    LOG.debug("Page not found: [{}] {}", publicationId, path);
                    return null;
                } catch (FilterException | ParseException | SerializationException | IOException e) {
                    throw new ContentProviderException("Exception while getting page model for: [" + publicationId +
                            "] " + path, e);
                }

                return pageBuilder.createPage(genericPage, localization, DD4TContentProvider.this);
            }
        });
    }

    @Override
    public InputStream getPageContent(String path, Localization localization) throws ContentProviderException {
        return findPage(path, localization, new TryFindPage<InputStream>() {
            @Override
            public InputStream tryFindPage(String path, int publicationId) throws ContentProviderException {
                final String pageContent;
                try {
                    pageContent = dd4tPageFactory.findPageContentByUrl(path, publicationId);
                } catch (ItemNotFoundException e) {
                    LOG.debug("Page not found: [{}] {}", publicationId, path);
                    return null;
                } catch (FilterException | ParseException | SerializationException | IOException e) {
                    throw new ContentProviderException("Exception while getting page content for: [" +  publicationId +
                            "] " + path, e);
                }

                // NOTE: This assumes page content is always in UTF-8 encoding
                return new ByteArrayInputStream(pageContent.getBytes(StandardCharsets.UTF_8));
            }
        });
    }

    private static <T> T findPage(String path, Localization localization, TryFindPage<T> callback)
            throws ContentProviderException {
        String processedPath = processPath(path);
        final int publicationId = Integer.parseInt(localization.getId());

        LOG.debug("Try to find page: [{}] {}", publicationId, processedPath);
        T page = callback.tryFindPage(processedPath, publicationId);
        if (page == null && !path.endsWith("/") && !hasExtension(path)) {
            processedPath = processPath(path + "/");
            LOG.debug("Try to find page: [{}] {}", publicationId, processedPath);
            page = callback.tryFindPage(processedPath, publicationId);
        }

        if (page == null) {
            throw new PageNotFoundException("Page not found: [" + publicationId + "] " + processedPath);
        }

        return page;
    }

    private static String processPath(String path) {
        if (Strings.isNullOrEmpty(path)) {
            return DEFAULT_PAGE_NAME;
        }
        if (path.endsWith("/")) {
            path = path + DEFAULT_PAGE_NAME;
        }
        if (!hasExtension(path)) {
            path = path + DEFAULT_PAGE_EXTENSION;
        }
        return path;
    }

    private static boolean hasExtension(String path) {
        return path.lastIndexOf('.') > path.lastIndexOf('/');
    }
}
