package com.example.xiaoji.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.xiaoji.common.R;
import com.example.xiaoji.entity.AddressBook;

import java.util.List;

public interface AddressBookService extends IService<AddressBook> {
    R<AddressBook> saveAddressBook(AddressBook addressBook);

    R<AddressBook> updateAddressBook(AddressBook addressBook);

    R<String> deleteAddressBook(Long ids);

    R<AddressBook> setDefaultAddressBook(AddressBook addressBook);

    R get(Long id);

    R<AddressBook> getDefault();

    R<List<AddressBook>> getByUserId(AddressBook addressBook);
}
