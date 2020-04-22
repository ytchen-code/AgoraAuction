struct PaxosData {
    1: i32 n,
    2: Value v
    3: bool nack;
}

struct Value {
    1: string action, // "CREATE", "DELETE", "BID", "CLOSE"
    2: string itemName, // used for DELETE, CLOSE
    3: User user, // used for DELETE, CLOSE
    4: Bid bid, // used for BID
    5: Item item // used for CREATE
}

struct Bid {
    1: string itemName,
    2: i32 price,
    3: User bidder
}

struct User {
    1: string name,
    2: string uuid
}

struct Item {
    1: string itemName,
    2: i32 price,
    3: User creator,
}

struct Auction {
    1: Item item,
    2: User highestBidder,
    3: bool open,
    4: i32 timeout,
}

service ThriftPaxos {
    PaxosData prepare(1: PaxosData pd),
    PaxosData accept(1: PaxosData pd),

    map<string, Auction> getAllAuctions(),

    bool bid(1: string itemName, 2: User user, 3: i32 price),

    bool createAuction(1: Item item, 2: User user),
    bool delAuction(1: string itemName, 2:User user)
    bool closeAuction(1: string itemName, 2:User user)
}