package ru.vyarus.dropwizard.orient.internal.cmd;

import com.orientechnologies.orient.server.network.protocol.http.OHttpRequest;
import com.orientechnologies.orient.server.network.protocol.http.OHttpResponse;
import com.orientechnologies.orient.server.network.protocol.http.command.OServerCommandAbstract;

/**
 * Orient studio tries to reach orient rest api on "/api/*" path, when studio url doesn't contain
 * index.html (with index, it calls orient rest on "/" which is correct). Command will redirect
 * all calls to "/api/*" to "/" in order to fix studio behaviour (which is quite confusing).
 *
 * @author Vyacheslav Rusakov
 * @since 21.02.2017
 */
public class ApiRedirectCommand extends OServerCommandAbstract {

    @Override
    public boolean execute(final OHttpRequest iRequest, final OHttpResponse iResponse) throws Exception {
        iRequest.setUrl(iRequest.getUrl().replaceAll("^/api/", "/"));
        return true;
    }

    @Override
    public String[] getNames() {
        return new String[]{"GET|api/*", "PUT|api/*", "POST|api/*", "DELETE|api/*"};
    }
}
