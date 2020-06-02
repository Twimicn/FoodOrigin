pragma solidity >=0.4.22 <0.7.0;

contract FoodOrigin {

    // 用户结构体
    struct UserInfo{
        address id;     // 用户钱包地址
        string name;    // 用户名称
        int16 role;     // 用户角色
    }

    // 操作结构体
    struct OpInfo{
        address operator;   // 操作者名称
        string action;      // 操作
        string data;        // 操作的数据
    }

    // 商品结构体
    struct ItemInfo{
        string id;          // 商品ID
        string name;        // 商品名
    }

    mapping(address=>UserInfo) users;   // 用户列表
    mapping(string=>ItemInfo) items;    // 商品列表
    mapping(string=>OpInfo) ops;        // 操作列表

    address _admin_id;                  // 管理员钱包地址


    // 智能合约构造函数
    constructor() public {
        _admin_id = msg.sender;
    }


    // 根据钱包ID获取用户信息
    function getUser(address uid) public view returns (string memory name,int16 role){
        return (users[uid].name,users[uid].role);
    }

    // 注册用户信息
    function setUser(address uid,string memory name,int16 role) public payable{
        users[uid].id = uid;
        users[uid].name = name;
        users[uid].role = role;
    }

    // 根据商品ID获取商品名称
    function getItem(string memory iid) public view returns (string memory name){
        return (items[iid].name);
    }

    // 添加一个商品
    function addItem(string memory iid,string memory name) public payable{
        items[iid].id = iid;
        items[iid].name = name;
    }

    // 添加商品的操作
    function addOp(string memory iid,string memory action,string memory data) public payable{
        bytes memory b;
        b = abi.encodePacked(iid);
        b = abi.encodePacked(b, "-");
        b = abi.encodePacked(b, action);
        string memory k = string(b);
        ops[k].operator = msg.sender;
        ops[k].action = action;
        ops[k].data = data;
    }

    // 根据商品ID和操作名称获取操作者地址和数据
    function getOps(string memory iid,string memory action) public view returns(address uid,string memory data){
        bytes memory b;
        b = abi.encodePacked(iid);
        b = abi.encodePacked(b, "-");
        b = abi.encodePacked(b, action);
        string memory k = string(b);
        return (ops[k].operator,ops[k].data);
    }
}