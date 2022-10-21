package com.example.xiaoji.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.xiaoji.common.BaseContext;
import com.example.xiaoji.common.R;
import com.example.xiaoji.entity.AddressBook;
import com.example.xiaoji.mapper.AddressBookMapper;
import com.example.xiaoji.service.AddressBookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
    @Override
    public R<AddressBook> saveAddressBook(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        save(addressBook);
        return R.success(addressBook);
    }

    @Override
    public R<AddressBook> updateAddressBook(AddressBook addressBook) {
        updateById(addressBook);
        return R.success(addressBook);
    }

    @Override
    public R<String> deleteAddressBook(Long ids) {
        removeById(ids);
        return R.success("删除成功");
    }

    @Transactional
    @Override
    public R<AddressBook> setDefaultAddressBook(AddressBook addressBook) {
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault, 0);
        //SQL:update address_book set is_default = 0 where user_id = ?
        update(wrapper);
        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        updateById(addressBook);
        return R.success(addressBook);
    }

    @Override
    public R get(Long id) {
        AddressBook addressBook = getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }
    }

    @Override
    public R<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        queryWrapper.eq(AddressBook::getIsDefault, 1);
        //SQL:select * from address_book where user_id = ? and is_default = 1
        AddressBook addressBook = getOne(queryWrapper);

        if (null == addressBook) {
            return R.error("没有找到该对象");
        } else {
            return R.success(addressBook);
        }
    }

    @Override
    public R<List<AddressBook>> getByUserId(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        queryWrapper.orderByDesc(AddressBook::getUpdateTime);
        //SQL:select * from address_book where user_id = ? order by update_time desc
        return R.success(list(queryWrapper));
    }
}
