package com.restdude.domain.base.controller;


import com.restdude.util.exception.http.HttpException;
import org.resthub.common.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Set;

/**
 * Basic model-based REST controller interface
 *
 * @param <T>  The model resource (POJO entity or DTO etc.) class
 * @param <ID> The primary resource identifier applicable for the model resource
 */
public interface IRestController<T, ID extends Serializable> {

    /**
     * Create a new resource<br />
     * REST webservice published : POST /
     *
     * @param resource The resource to create
     * @return CREATED http status code if the request has been correctly processed, with updated resource enclosed in the body, usually with and additional identifier automatically created by the database
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    T create(@RequestBody T resource) throws HttpException;

    /**
     * Update an existing resource<br/>
     * REST webservice published : PUT /{id}
     *
     * @param id       The identifier of the resource to update, usually a Long or String identifier. It is explicitely provided in order to handle cases where the identifier could be changed.
     * @param resource The resource to update
     * @return OK http status code if the request has been correctly processed, with the updated resource enclosed in the body
     * @throws NotFoundException
     */
    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    @ResponseBody
    T update(@PathVariable ID id, @RequestBody T resource) throws HttpException;

    /**
     * Find all resources, and return the full collection (plain list not paginated)<br/>
     * REST webservice published : GET /?page=no
     *
     * @return OK http status code if the request has been correctly processed, with the list of all resource enclosed in the body.
     * Be careful, this list should be big since it will return ALL resources. In this case, consider using paginated findAll method instead.
     */
    @RequestMapping(method = RequestMethod.GET, params = "page=no")
    @ResponseBody
    Iterable<T> findAll() throws HttpException;

    /**
     * Find all resources, and return a paginated and optionaly sorted collection
     *
     * @param page       Page number starting from 0. default to 0
     * @param size       Number of resources by pages. default to 10
     * @param direction  Optional sort direction, could be "ASC" or "DESC"
     * @param properties Ordered list of comma separated property names used for sorting results. At least one property should be provided if direction is specified
     * @return OK http status code if the request has been correctly processed, with the a paginated collection of all resource enclosed in the body.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    Page<T> findPaginated(@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
                          @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
                          @RequestParam(value = "direction", required = false, defaultValue = "ASC") String direction,
                          @RequestParam(value = "properties", required = false) String properties) throws HttpException;

    /**
     * Find a resource by its identifier<br/>
     * REST webservice published : GET /{id}
     *
     * @param id The identifier of the resouce to find
     * @return OK http status code if the request has been correctly processed, with resource found enclosed in the body
     * @throws NotFoundException
     */
    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    @ResponseBody
    T findById(@PathVariable ID id) throws HttpException;

    /**
     * Find multiple resources by their identifiers<br/>
     * REST webservice published : GET /?ids[]=
     * <p/>
     * example : /?ids[]=1&ids[]=2&ids[]=3
     *
     * @param ids List of ids to retrieve
     * @return OK http status code with list of retrieved resources. Not found resources are ignored:
     * no Exception thrown. List is empty if no resource found with any of the given ids.
     */
    @RequestMapping(method = RequestMethod.GET, params = "ids[]")
    @ResponseBody
    Iterable<T> findByIds(@RequestParam(value = "ids[]") Set<ID> ids) throws HttpException;

    /**
     * Delete all resources<br/>
     * REST webservice published : DELETE /<br/>
     * Return No Content http status code if the request has been correctly processed
     */
    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete() throws HttpException;

    /**
     * Delete a resource by its identifier<br />
     * REST webservice published : DELETE /{id}<br />
     * Return No Content http status code if the request has been correctly processed
     *
     * @param id The identifier of the resource to delete
     * @throws NotFoundException
     */
    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable ID id) throws HttpException;

}