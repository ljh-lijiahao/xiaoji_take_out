package com.example.xiaoji.controller;

import com.example.xiaoji.common.R;
import com.example.xiaoji.entity.AddressBook;
import com.example.xiaoji.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址簿管理
 */
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增地址
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        return addressBookService.saveAddressBook(addressBook);
    }

    /**
     * 修改地址
     */
    @PutMapping
    public R<AddressBook> update(@RequestBody AddressBook addressBook) {
        return addressBookService.updateAddressBook(addressBook);
    }

    /**
     * 删除地址
     */
    @DeleteMapping
    public R<String> delete(Long ids) {
        return addressBookService.deleteAddressBook(ids);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        return addressBookService.setDefaultAddressBook(addressBook);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        return addressBookService.get(id);
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    public R<AddressBook> getDefault() {
        return addressBookService.getDefault();
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {
        return addressBookService.getByUserId(addressBook);
    }
}
